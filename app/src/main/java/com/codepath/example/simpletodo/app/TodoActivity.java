package com.codepath.example.simpletodo.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TodoActivity extends ActionBarActivity {

    private final int REQUEST_CODE = 1;

    private ArrayList<String> todoItems;
    private ArrayAdapter<String> todoAdapter;      // translate array of data to list view
    private ListView lvItems;
    private EditText etNewItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        readItems();

        etNewItem = (EditText) findViewById(R.id.etNewItem);
        lvItems = (ListView) findViewById(R.id.lvItems);
        todoAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, todoItems);

        lvItems.setAdapter(todoAdapter);

        setupListViewListener();
        launchEditItem();
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View item, int position, long id) {
                todoItems.remove(position);
                todoAdapter.notifyDataSetChanged();
                writeItems();
                return true;
            }
        });
    }

    // Feature to edit when user click on item.
    private void launchEditItem() {
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(view.getContext(), EditItemActivity.class);
                String itemText = todoItems.get(position).toString();
                i.putExtra("itemText", itemText);
                i.putExtra("itemPosition", position);
                startActivityForResult(i, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract item text and item position in list from result extras
            int editedItemPosition = data.getExtras().getInt("editedItemPosition");
            String editedItemText = data.getExtras().getString("editedItemText");
            String originalItemText = todoItems.get(editedItemPosition);

            // only update item if there are changes
            if (!editedItemText.equals(originalItemText)) {
                // if edited text becomes blank, remove item
                if(editedItemText.equals("")) {
                    todoItems.remove(editedItemPosition);
                    todoAdapter.notifyDataSetChanged();
                    writeItems();
                    return;
                }

                todoItems.set(editedItemPosition, editedItemText);
                todoAdapter.notifyDataSetChanged();
                writeItems();
            }
        }
    }

    public void addTodoItem(View v) {
        String itemText = etNewItem.getText().toString();
        todoAdapter.add(itemText);
        writeItems();

        // clear text field after item is added
        etNewItem.setText("");
    }

    private void readItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            todoItems = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (IOException e) {
            todoItems = new ArrayList<String>();
            e.printStackTrace();
        }
    }

    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            FileUtils.writeLines(todoFile, todoItems);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds todoItems to the action bar if it is present.
        getMenuInflater().inflate(R.menu.todo, menu);
        return true;
    }
}
