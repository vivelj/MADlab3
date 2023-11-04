package com.example.mlr3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;

public class task2 extends AppCompatActivity
{
    private EditText note1,note2,note3;
    private Button addItem,finish;

    private boolean flag_visible = true;//флаг видимости ActionBar
    private boolean flag_edit = false;//флаг для изменения инфы о товаре
    private int pos = 0;//позиция товара в списке

    SharedPreferences sharedPreferences;
    private ViewFlipper mViewFlipper;
    private static ArrayList<String> Name = new ArrayList<>();//наименование товара
    private static ArrayList<String> Price = new ArrayList<>();//цена товара за 1 шт
    private static ArrayList<String> Quantity = new ArrayList<>();//количество шт
    ListViewAdapter arrayAdapter = new ListViewAdapter(this, Name, Price, Quantity);

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_l3,menu);

        MenuItem shareItem = menu.findItem(R.id.delete_all);
        MenuItem shareItem1 = menu.findItem(R.id.add_new);
        if (!flag_visible)
        {
            shareItem.setVisible(false);
            shareItem1.setVisible(false);
        }
        else
        {
            shareItem.setVisible(true);
            shareItem1.setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        //удаление всех даных в заказе
        if (item.getItemId() == R.id.delete_all)
        {
            new AlertDialog.Builder(task2.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Вы хотите удалить все записи?")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            Name.clear();
                            Price.clear();
                            Quantity.clear();
                            arrayAdapter.notifyDataSetChanged();
                            sharedPreferences = getApplicationContext().
                                    getSharedPreferences("com.example.LR3", Context.MODE_PRIVATE);
                            sharedPreferences.edit().clear().apply();
                        }
                    })
                    .setNegativeButton("Нет",null)
                    .show();

            return true;
        }
        //добавление нового товара в заказ
        if (item.getItemId() == R.id.add_new)
        {
            addItem.setText("Добавить");
            finish.setVisibility(View.VISIBLE);
            showNextScreen();
            flag_visible = false;
            invalidateOptionsMenu();//содержимое меню изменилось, и меню должно быть перерисовано
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task2);
        getSupportActionBar().setTitle("Заказ");

        Name.clear();
        Price.clear();
        Quantity.clear();

        //загрузка сохраненных элементов, если таковые были
        sharedPreferences = getApplicationContext().
                getSharedPreferences("com.example.LR3", Context.MODE_PRIVATE);

        View.OnClickListener mOnClickListener = new ClickListener();
        mViewFlipper = (ViewFlipper) findViewById(R.id.new_container);

        note1 = (EditText) findViewById(R.id.Note1);
        note2 = (EditText) findViewById(R.id.Note2);
        note3 = (EditText) findViewById(R.id.Note3);

        finish = (Button) findViewById(R.id.btn_finish);
        addItem = (Button) findViewById(R.id.btn_add);

        finish.setOnClickListener(mOnClickListener);
        addItem.setOnClickListener(mOnClickListener);

        ListView listView = (ListView) findViewById(R.id.listView1);
        listView.setAdapter(arrayAdapter);


    }

    // перейти на предыдущий экран с анимацией справа налево
    private void showPreviousScreen() {
        // переход влево доступен только если мы не на первом экране
        if (!isFirst()) {
            mViewFlipper.setInAnimation(this, R.anim.right_in);
            mViewFlipper.setOutAnimation(this, R.anim.right_out);
            mViewFlipper.showPrevious();
        }
        else {
            finish();
            overridePendingTransition(R.anim.fall_above,R.anim.fall_down);
        }
    }

    // определяем, является ли текущий экран первым
    private boolean isFirst() {
        return mViewFlipper.getDisplayedChild() == 0;
    }

    // перейти на следующий экран с анимацией слева на право
    private void showNextScreen() {
        // переход вправо доступен только если мы не на последнем экране
        if (!isLast()) {
            mViewFlipper.setInAnimation(this, R.anim.left_in);
            mViewFlipper.setOutAnimation(this, R.anim.left_out);
            // переход вправо доступен
            mViewFlipper.showNext();
        }
    }

    // определяем, является ли текущий экран последним
    private boolean isLast() {
        return mViewFlipper.getDisplayedChild() + 1 == mViewFlipper.getChildCount();
    }

    private class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int id = view.getId();
            //нажатие кнопки Добавить/Изменить
            if (id == R.id.btn_add)
            {   //проверка на введение данных
                if (    note1.getText().toString().equals("") ||
                        note2.getText().toString().equals("") ||
                        note3.getText().toString().equals(""))
                {
                    Toast.makeText(task2.this, "Заполни все поля!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //Если был выбран пункт Добавить новый товар, то появляется лэйоут
                    // для добваления товара
                    if (!flag_edit)
                    {
                        Name.add(note1.getText().toString());
                        Price.add(note2.getText().toString());
                        Quantity.add(note3.getText().toString());
                        note1.setText("");
                        note2.setText("");
                        note3.setText("");
                    }
                    //Если был короткий клик на элемент, то меняется лэйаут
                    // для изменения информации об этом товаре
                    else
                    {
                        flag_edit = false;
                        Name.set(pos, note1.getText().toString());
                        Price.set(pos, note2.getText().toString());
                        Quantity.set(pos, note3.getText().toString());
                        note1.setText("");
                        note2.setText("");
                        note3.setText("");
                        showPreviousScreen();
                        flag_visible = true;
                        invalidateOptionsMenu();
                    }
                    //запись измененной строки в хранилище
                    sharedPreferences = getApplicationContext()
                            .getSharedPreferences("com.example.LR3", Context.MODE_PRIVATE);
                    StringBuilder notesSaved = new StringBuilder();
                    for (int j = 0; j < Name.size(); j++)
                    {
                        notesSaved.append(Name.get(j)).append(" ").append(Price.get(j)).append(" ").append(Quantity.get(j)).append(" ");
                    }
                    sharedPreferences.edit().putString("notesSaved", notesSaved.toString()).apply();
                }
            }

            //нажатие кнопки Завершить заказ
            else if (id == R.id.btn_finish)
            {
                note1.setText("");
                note2.setText("");
                note3.setText("");
                showPreviousScreen();
                flag_visible = true;
                invalidateOptionsMenu();
            }
            arrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        showPreviousScreen();
        flag_visible = true;
        invalidateOptionsMenu();
    }

    private static class ListViewAdapter extends BaseAdapter
    {
        private Activity context;
        private ArrayList<String> title;
        private ArrayList<String> description;
        private ArrayList<String> cost;

        public ListViewAdapter(Activity context, ArrayList<String> title, ArrayList<String> description, ArrayList<String> cost) {
            super();
            this.context = context;
            this.title = title;
            this.description = description;
            this.cost = cost;
        }

        public int getCount() {
            return title.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        private static class ViewHolder {
            TextView txt1;
            TextView txt2;
            TextView txt3;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            LayoutInflater inflater =  context.getLayoutInflater();

            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.listitem_row, null);
                holder = new ViewHolder();
                holder.txt1 = (TextView) convertView.findViewById(R.id.textView1);
                holder.txt2 = (TextView) convertView.findViewById(R.id.textView2);
                holder.txt3 = (TextView) convertView.findViewById(R.id.textView3);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txt1.setText(title.get(position));
            holder.txt2.setText(description.get(position));
            holder.txt3.setText(cost.get(position));

            return convertView;
        }
    }
}