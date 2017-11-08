package com.calonga.tutorialcloudfirestore;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.calonga.tutorialcloudfirestore.adapters.ListItemAdapter;
import com.calonga.tutorialcloudfirestore.model.ToDo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    private final static String TITULO = "titulo";
    private final static String DESCRICAO = "descricao";
    private final static String TODOLIST = "ToDoList";

    List<ToDo> toDoList = new ArrayList<>();
    FirebaseFirestore db;

    RecyclerView listaItem;
    RecyclerView.LayoutManager layoutManager;

    FloatingActionButton fab;

    public MaterialEditText titulo,descricao; // publico para acessar o ListAdapter
    public  boolean isUpdate = false; // flag para checar alterações a novos adicionados;
    public String idUpdate = ""; // Id do item caso necessário update

    ListItemAdapter adapter;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        //View
        dialog = new SpotsDialog(this);
        titulo = findViewById(R.id.titulo);
        descricao = findViewById(R.id.descricao);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Adicionar novo
                if(!isUpdate){
                    setDados(titulo.getText().toString(),descricao.getText().toString());
                }else {
                    alterarDados(titulo.getText().toString(),descricao.getText().toString());
                    isUpdate = !isUpdate; //Reseta a flas
                }
            }
        });

        listaItem = findViewById(R.id.listaTodo);
        listaItem.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listaItem.setLayoutManager(layoutManager);

        carregarDados();

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals("DELETE"))
            deleteItem(item.getOrder());
        return super.onContextItemSelected(item);

    }

    private void deleteItem(int index) {
        db.collection(TODOLIST).document(toDoList.get(index).getId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        carregarDados();
                    }
                });
    }

    private void alterarDados(String titulo, String descricao) {
        db.collection(TODOLIST).document(idUpdate).update(TITULO,titulo, DESCRICAO,descricao).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, "Alterado !", Toast.LENGTH_SHORT).show();
            }
        });

        //Alterar dados em tempo real
        db.collection(TODOLIST).document(idUpdate).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                carregarDados();
            }
        });
    }

    private void setDados(String titulo, String descricao) {
        //Id Randomico
        String id = UUID.randomUUID().toString();
        Map<String,Object> todo = new HashMap<>();
        todo.put("id",id);
        todo.put(TITULO,titulo);
        todo.put(DESCRICAO,descricao);

        db.collection(TODOLIST).document(id).set(todo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Atualiza dados
                carregarDados();
            }
        });

    }

    private void carregarDados() {
        dialog.show();
        if (toDoList.size() > 0){
            toDoList.clear(); // Limpa valores antigos
        }
        db.collection(TODOLIST).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot doc: task.getResult()){
                    ToDo todo = new ToDo(doc.getString("id"),
                            doc.getString(TITULO),
                            doc.getString(DESCRICAO));
                    toDoList.add(todo);
                }
                adapter = new ListItemAdapter(MainActivity.this,toDoList);
                listaItem.setAdapter(adapter);
                dialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
