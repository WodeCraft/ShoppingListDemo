package org.projects.shoppinglist;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.projects.shoppinglist.fragments.ConfirmDialogFragment;
import org.projects.shoppinglist.models.Product;

import java.util.ArrayList;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity implements ConfirmDialogFragment.OnPositiveListener {

    private final int RESULT_CODE_PREFERENCES = 1;

    private DatabaseReference firebase;

    FirebaseListAdapter<Product> adapter;
    ListView listView;
    ArrayList<Product> bag = new ArrayList<>();
    String amount;

//    public ArrayAdapter getMyAdapter()
//    {
//        return adapter;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Needed to get the toolbar to work on older versions
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            ArrayList<Product> savedBag = savedInstanceState.getParcelableArrayList("bag");
            if (savedBag != null) {
                bag = savedBag;
            }
        }

        // Setting up Firebase and adapter
        firebase = FirebaseDatabase.getInstance().getReference().child("products");
        Query query = FirebaseDatabase.getInstance().getReference().child("products");

        FirebaseListOptions<Product> options = new FirebaseListOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .setLayout(android.R.layout.simple_list_item_checked)
                .build();

        adapter = new FirebaseListAdapter<Product>(options) {
            @Override
            protected void populateView(View v, Product product, int position) {
                TextView textView = v.findViewById(android.R.id.text1);
                textView.setTextSize(24);
                textView.setText(product.toString());
            }
        };

        //getting our listiew - you can check the ID in the xml to see that it
        //is indeed specified as "list"
        listView = findViewById(R.id.list);

        //setting the adapter on the listview
        listView.setAdapter(adapter);
        //here we set the choice mode - meaning in this case we can
        //only select one item at a time.
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        Button addButton =  findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductToBag();
            }
        });

        // TODO Implement these buttons on the Portrait layout as well
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Spinner amountSpinner = findViewById(R.id.quantitySpinner);

            ArrayAdapter<CharSequence> amountSpinnerAdapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.quantitySpinnerArray,
                    android.R.layout.simple_spinner_item);
            amountSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            amountSpinner.setAdapter(amountSpinnerAdapter);
            amountSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String[] listArray = getResources().getStringArray(R.array.quantitySpinnerArray);
                    amount = listArray[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            Button clearListButton = findViewById(R.id.clearList);
            clearListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearList();
                }
            });

            Button deleteItemButton = findViewById(R.id.deleteItem);
            deleteItemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem();
                    if (bag.size() <= 0) {
                        enableButtons(false);
                    }
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivityForResult(intent, RESULT_CODE_PREFERENCES);
                break;
            case R.id.action_clearAll:
                clearList();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //ALWAYS CALL THE SUPER METHOD - To be nice!
        super.onSaveInstanceState(outState);
		/* Here we put code now to save the state */
		outState.putParcelableArrayList("bag", bag);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // INFO This method could be used to restore saved data instead of doing it in onCreate()
        //      Require a call to adapter.notifyOnDataSetChanged().
    }

    private void addProductToBag() {
        EditText productToAdd = findViewById(R.id.productName);
        EditText quantityField = findViewById(R.id.quantity);

        int quantity;
        if (amount != "") {
            quantity = Integer.parseInt(amount);
        } else {
            quantity = Integer.parseInt(quantityField.getText().toString());
        }
        String name = productToAdd.getText().toString();

        if (quantity > 0 && name != "") {
            Product product = new Product(name, quantity);

            firebase.push().setValue(product);
            adapter.notifyDataSetChanged();

            productToAdd.setText("");
            quantityField.setText("");
            amount = "";

            enableButtons(true);
        }

    }

    private void clearList() {
        ConfirmDialogFragment dialog = new ConfirmDialogFragment();
        dialog.show(getFragmentManager(), "MyFragment");
    }

    /**
     * This method handles positive click events from the ConfirmDialogFragment
     */
    @Override
    public void onPositiveClicked() {
        bag.clear();
        adapter.notifyDataSetChanged();
        enableButtons(false);
    }


    private void deleteItem() {
        ListView list = findViewById(R.id.list);
        int indexSelected = list.getCheckedItemPosition();
        if (indexSelected >= 0 && indexSelected < bag.size()) {
            bag.remove(indexSelected);
            adapter.notifyDataSetChanged();
        }
    }

    private void enableButtons(boolean enabled) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Button clearButton = findViewById(R.id.clearList);
            clearButton.setEnabled(enabled);
            Button deleteButton = findViewById(R.id.deleteItem);
            deleteButton.setEnabled(enabled);


        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
