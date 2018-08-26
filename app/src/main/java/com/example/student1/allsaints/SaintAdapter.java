package com.example.student1.allsaints;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class SaintAdapter  extends ArrayAdapter<Saint> {
    private Context context;
    private int resource;
    private List<Saint> data;
    private LayoutInflater inflater;

    // Сет для хранения номеров выбранных элементов
    private HashSet<Integer> selection;

    // Так как класс расширяет ArrayAdapter
    // он должен иметь перегруженный конструктор в котором вызывать
    // один из конструкторов суперкласса
    public SaintAdapter(Context context, int resource, List<Saint> data) {
        super(context, resource);

        this.context = context;
        this.resource = resource;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    // Говорит listview, сколько будет типов элементов
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    // Хелпер чтобы определить, выделены ли
    // какие-нибудь элементы listview.
    boolean hasSelected()
    {
        return !selection.isEmpty();
    }

    // Хелпер - выделен ли конкретный элемент.
    boolean isSelected(int position)
    {
        return selection.contains(position);
    }

    // Выделение - если элемент выделен, сделать не выделенным;
    // если не выделен - выделить.
    // В любом случае при изменении статуса уведомить об этом
    // адаптер.
    public void toggleSelection(int position) {
        if (isSelected(position)) {
            selection.remove(position);
        }
        else {
            selection.add(position);
        }
        notifyDataSetChanged();
    }

    // Количество элементов в контейнере
    @Override
    public int getCount() {
        return data.size();
    }

    // Удалить выделенные элементы
    public void deleteSelected() {

        List<Integer> items = new ArrayList<>();

        // Вначале формируем List из сета.
        items.addAll(selection);

        // Обратно сортируем этот лист, чтобы
        // вначале удалять элемент с самым большим
        // номером.
        Collections.sort(items);
        Collections.reverse(items);

        // Удаляем как сам элемент так и
        // его признак выделенности.
        for(int i: items) {
            selection.remove(i);
            data.remove(i);
        }

        // Уведомляем об этом адаптер.
        notifyDataSetChanged();
    }

    // Шаблон ViewHolder
    // Подробнее https://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
    static class Holder
    {
        public TextView name;
        public TextView dob;
        public TextView dod;
        public RatingBar bar;
        public ImageView button;
    }

    // Возвращает тип элемента
    @Override
    public int getItemViewType(int position) {
        if(isSelected(position))
            return 1;
        else
            return 0;
    }

    // Возвращение нового или переопределенного View для ListView
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        // View rowView = inflater.inflate(R.layout.listviewitem, parent, false);
        View rowView = convertView;
        Holder holder;

        // Если переданный нам View нулевой, нужно:
        // 1. Создать его
        // 2. Найти ссылки на его поля
        // 3. Создать ViewHolder
        // 4. Присвоить ссылкам ViewHolder ссылки на поля View
        // 5. Созранить созданный ViewHolder в Tag созданного View
        if(rowView == null)
        {
            // Создаем элемент в зависимости от его типа.
            // ListView сам следить за тем, чтобы
            // передать элемент правильного типа в convertView.
            if(isSelected(position))
                rowView = inflater.inflate(R.layout.listviewitemselected, parent, false);
            else
                rowView = inflater.inflate(R.layout.listviewitem, parent, false);

            TextView name = (TextView) rowView.findViewById(R.id.text);
            TextView dob = (TextView) rowView.findViewById(R.id.dob);
            TextView dod = (TextView) rowView.findViewById(R.id.dod);
            RatingBar bar = (RatingBar) rowView.findViewById(R.id.rating);
            ImageView button = (ImageView)  rowView.findViewById(R.id.threedots);

            holder = new Holder();

            holder.name = name;
            holder.dob = dob;
            holder.dod = dod;
            holder.bar = bar;
            holder.button = button;

            rowView.setTag(holder);
        }
        // Если View уже был создан
        // Просто получить ссылку на ViewHolder, хранящийся в его Tag
        else
        {
            holder = (Holder) rowView.getTag();
        }

        // View любо пустой, либо содержит уже не актуальные данные
        // Загрузим Святого
        Saint s = data.get(position);

        // Актуализируем данные View через ссылки ViewHolder
        holder.name.setText(s.getName());
        holder.dob.setText(s.getDob());
        holder.dod.setText(s.getDod());
        holder.bar.setRating(s.getRating());

        if(hasSelected())
            holder.button.setVisibility(View.INVISIBLE);
        else
            holder.button.setVisibility(View.VISIBLE);

        // Реакция на click на картинку
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Отобразим popup меню.
                showPopupMenu(context, v, position);
            }
        });

        return rowView;
    }

    // Хелпер для запуска popup меню.
    // Оно используется так как
    // если зарегистрировать для listview "длинный" клик,
    // контекстное меню больше не показывается.
    private void showPopupMenu(Context con, View v, final int pos) {
        // Отображаем меню только если никакой элемент не выбран.
        if(!hasSelected()) {
            PopupMenu popupMenu = new PopupMenu(con, v);
            popupMenu.inflate(R.menu.context);

            popupMenu
                    .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.context_delete:
                                    selection.remove(pos);
                                    data.remove(pos);
                                    notifyDataSetChanged();
                                    return true;
                            }
                            return false;
                        }
                    });

            popupMenu.show();
        }
    }
}
