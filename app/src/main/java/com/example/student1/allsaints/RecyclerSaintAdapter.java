package com.example.student1.allsaints;

import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


public class RecyclerSaintAdapter extends RecyclerView.Adapter<SaintHolder> {



    public void ratingChanged(int position, float newRating) {
        saints.get(position).setRating(newRating);
        // notifyItemChanged(position);
    }

    public static interface OnItemClickListener {
        public void onItemClick(View itemView, int position);
    }


    public static interface OnItemLongClickListener {
        public void onItemLongClick(View itemView, int position);
    }

    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    void setOnItemClickListener(OnItemClickListener listener)
    {
        this.clickListener = listener;
    }

    void setOnItemLongClickListener(OnItemLongClickListener listener)
    {
        this.longClickListener = listener;
    }

    private HashSet<Integer> selection = new HashSet<>();


    private List<Saint> saints;

    public RecyclerSaintAdapter(final List<Saint> saints)
    {
        this.saints = saints;
    }

    @Override
    public SaintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = null;
        switch (viewType)
        {
            case 0:
                view = inflater.inflate(R.layout.listviewitem, parent, false);
                break;
            case 1:
                view = inflater.inflate(R.layout.listviewitemselected, parent, false);
                break;
        }

        return new SaintHolder(view, this, clickListener, longClickListener);
    }

    public void remove(int pos)
    {
        selection.remove(pos);
        saints.remove(pos);
        notifyItemRemoved(pos);
    }


    @Override
    public void onBindViewHolder(SaintHolder holder, int position) {
        Saint s = saints.get(position);

        // Актуализируем данные View через ссылки ViewHolder
        holder.name.setText(s.getName());
        holder.dob.setText(s.getDob());
        holder.dod.setText(s.getDod());
        holder.bar.setRating(s.getRating());

    }

    @Override
    public int getItemCount() {
        return saints.size();
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
        notifyItemChanged(position);
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
            saints.remove(i);
        }

        // Уведомляем об этом адаптер.
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(isSelected(position))
            return 1;
        else
            return 0;

    }
}
