package com.example.student1.allsaints;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // Константы для передачи данных через Intent 
    // Доджны быть доступны детальной Activity
    public static final String SAINT_NAME = "SAINT_NAME";
    public static final String SAINT_ID = "SAINT_ID";
    public static final String SAINT_RATING = "SAINT_RATING";
    public static final int RATING_REQUEST = 777;

    private List<Saint> data = new ArrayList<>();
    private ListView list;

    // private List<String> saints = new ArrayList<>();
    // private ArrayAdapter<String> adapter;
    private SaintAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.list);

        // Источник данных для парсера XML из ресурсов
        InputSource mySaints = new InputSource(getResources().openRawResource(R.raw.saints));
        // Новый XPath запрос
        XPath xPath = XPathFactory.newInstance().newXPath();
        
        // Подробно об XPath
        // http://www.w3schools.com/xsl/xpath_syntax.asp

        // Собственно запрос 
        String  expression = "/saints/saint";

        NodeList nodes;
        try {
            // Результат XPath запроса - набор узлов
            nodes = (NodeList) xPath.evaluate(expression, mySaints, XPathConstants.NODESET);
            if(nodes != null) {
                int numSaints = nodes.getLength();
                // Для каждого из узлов 
                for (int i = 0; i < numSaints; i++)
                {
                    // Узел
                    Node saint = nodes.item(i);

                    // Дочерние элементы
                    String name = saint.getFirstChild().getTextContent();
                    String dob = saint.getChildNodes().item(1).getTextContent();
                    String dod = saint.getChildNodes().item(2).getTextContent();

                    Log.d("happy", name);

                    Saint s = new Saint(name, dob, dod, 0f);

                    // saints.add(name);
                    data.add(s);
                }
            }
        }
        catch (Exception e)
        {

        }

        // Стандартный ArrayAdapter - подходит для 1 или 2 текстовых полей
        /*
        adapter = new ArrayAdapter<String>(
                this,
                R.layout.saint,
                R.id.text,
                saints
        );
        */

        // Нестадартный адаптер - 3 текстовых поля, рейтинг и картинка с листенером
        adapter = new SaintAdapter(this, R.layout.listviewitem, data);

        list.setAdapter(adapter);
        list.setOnItemClickListener(this);

        // Чтобы подписать ListView на стандартное контекстное меню по "длинному" щелчку
        // на элемент
        // registerForContextMenu(list);
    }

    // Вызывается при создании контекстного меню
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.context, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    // Вызывается при выборе элемента контекстного меню
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;

        /*
        String s = saints.get(position);
        //Snackbar.make(list, "Deleted: "+ s, Snackbar.LENGTH_SHORT).show();
        Toast.makeText(this, "Deleted: "+ s, Toast.LENGTH_SHORT).show();
        */
        Saint s = data.get(position);
        String name = s.getName();
        Toast.makeText(this, "Deleted: "+ name, Toast.LENGTH_SHORT).show();

        //saints.remove(position);
        data.remove(position);
        adapter.notifyDataSetChanged();

        return super.onContextItemSelected(item);
    }

    // По щелчку на элемент ListView
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //String saint = saints.get(position);

        Saint s = data.get(position);
        String saint = s.getName();

        // Создаем Intent на запуск детальной Activity
        Intent intent = new Intent(this, SaintDetail.class);

        // Добавляем туда нужные данные - имя, номер в контейнере и рейтинг
        intent.putExtra(SAINT_NAME, saint);
        intent.putExtra(SAINT_ID, position);
        intent.putExtra(SAINT_RATING, s.getRating());

        // Запускаем Activity и подписываемся на получение результата
        startActivityForResult(intent, RATING_REQUEST);
    }

    // Вызывается при создании меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Вызывается при выборе элемента меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_up:
                //Collections.sort(saints);
                // Чтобы отсортировать контейнер кастомного класса таким образом
                // класс должен имплементить интерфейс Comparable
                Collections.sort(data);
                adapter.notifyDataSetChanged();
                return true;

            case R.id.menu_down:
                //Collections.sort(saints, Collections.<String>reverseOrder());
                Collections.sort(data, Collections.<Saint>reverseOrder());
                adapter.notifyDataSetChanged();
                return true;
            case R.id.menu_add:
                showAddDialog();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    // Вызывается по нажатию на "+" меню
    private void showAddDialog() {
        // Для создания кастомного диалога нужно создать Layout XML файл
        // "Надуваем" view диалога используя xml файл
        View dialog = getLayoutInflater().inflate(R.layout.dialog_add, null);

        // Находим EditText используя только что "надутый" view
        final EditText text = (EditText) dialog.findViewById(R.id.dialog_add);

        // Создаем диалог используя Builder шаблон
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialog)
                .setTitle("Add a Saint!") // Заголовок диалога
                // "Отрицательная" кнопка и ее Listener
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel(); // Просто закрываем диалог
                    }
                })
                // "Положительная" кнопка - нужно добавить Святого
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Получаем имя
                        String saint = text.getText().toString();
                        // Создаем святого и добавляем его в контейнер
                        // Закрываем диалог
                        createSaint(new Saint(saint, "", "", 0f));
                        dialog.dismiss();
                    }
                })
                .create() // Создаем диалог из builder-а
                .show(); // Показываем его

    }

    private void createSaint(Saint saint) {
        // Добавляем Святого в контейнер
        data.add(saint);
        // Просим адаптер обновиться
        adapter.notifyDataSetChanged();
    }

    // Детальная Activity закрылась, получим ее результат
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int id = -1;
        float rating = -1f;
        // Проверяем Intent
        if(data != null)
        {
            // Проверяем, есть ли нужные данные
            if(data.hasExtra(SAINT_ID))
            {
                // Если есть, получаем их
                id = data.getIntExtra(SAINT_ID, -1);
            }
            // Проверяем, есть ли нужные данные
            if(data.hasExtra(SAINT_RATING))
            {
                // Если есть, получаем их
                rating = data.getFloatExtra(SAINT_RATING, -1f);
            }
            // Если данные валидны, обновляем запись в контейнере
            if(id >=0 && rating >= 0f)
            {
                this.data.get(id).setRating(rating);
                // Уведомляем адаптер о том, что данные в контейнере 
                // могли измениться
                adapter.notifyDataSetChanged();
            }
        }
    }
}
