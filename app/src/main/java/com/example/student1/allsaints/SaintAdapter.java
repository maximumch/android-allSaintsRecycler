package com.example.student1.allsaints;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

public class SaintAdapter  extends ArrayAdapter<Saint> {
    private Context context;
    private int resource;
    private List<Saint> data;
    private LayoutInflater inflater;

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

    // Количество элементов в контейнере
    @Override
    public int getCount() {
        return data.size();
    }

    // Получить элемент из контейнера через адаптер
    @Override
    public Saint getItem(int position) {
        return data.get(position);
    }

    // Идентификатор элемента контейнера
    @Override
    public long getItemId(int position) {
        return position;
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

    // Возвращение нового или переопределенного View для ListView
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

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
        Saint s = getItem(position);

        // Актуализируем данные View через ссылки ViewHolder
        holder.name.setText(s.getName());
        holder.dob.setText(s.getDob());
        holder.dod.setText(s.getDod());
        holder.bar.setRating(s.getRating());

        // Реакция на click на картинку
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Зарегистрируем "родителя" картинки на контекстное меню 
                ((AppCompatActivity)context).registerForContextMenu(parent);
                // Подожжем контекстное меню 
                parent.showContextMenuForChild(v);
                // Разрегистрируем родителя, чтобы контекстное меню не отображалось 
                // по "длинному" щелчку
                ((AppCompatActivity)context).unregisterForContextMenu(parent);
            }
        });

        return rowView;
    }
}
