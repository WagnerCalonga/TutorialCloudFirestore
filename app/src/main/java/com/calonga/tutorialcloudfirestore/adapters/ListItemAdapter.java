package com.calonga.tutorialcloudfirestore.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.calonga.tutorialcloudfirestore.MainActivity;
import com.calonga.tutorialcloudfirestore.R;
import com.calonga.tutorialcloudfirestore.model.ToDo;

import java.util.List;

/**
 * Created by Wagner on 07/11/2017.
 */

class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{

    ItemClickListener itemClickListener;
    TextView item_titulo, item_descricao;

    public ListItemViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);

        item_titulo = itemView.findViewById(R.id.item_titulo);
        item_descricao = itemView.findViewById(R.id.item_descricao);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Selecione a ação");
        menu.add(0,0,getAdapterPosition(),"DELETE");
    }
}

public class ListItemAdapter extends  RecyclerView.Adapter<ListItemViewHolder> {
   MainActivity mainActivity;
   List<ToDo> toDoList;

    public ListItemAdapter(MainActivity mainActivity, List<ToDo> toDoList) {
        this.mainActivity = mainActivity;
        this.toDoList = toDoList;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mainActivity.getBaseContext());
        View view = inflater.inflate(R.layout.item_lista,parent,false);

        return new ListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder holder, int position) {

        holder.item_titulo.setText(toDoList.get(position).getTitulo());
        holder.item_descricao.setText(toDoList.get(position).getDescricao());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongoClick) {
                //Quando usuario selecionar item, os dados vao auto setar o para o edtText
                mainActivity.titulo.setText(toDoList.get(position).getTitulo());
                mainActivity.descricao.setText(toDoList.get(position).getDescricao());

                mainActivity.isUpdate=true; //Setar flag indicando que foi alterado os dados
                mainActivity.idUpdate = toDoList.get(position).getId();

            }
        });
    }

    @Override
    public int getItemCount() {
        return toDoList.size();
    }
}
