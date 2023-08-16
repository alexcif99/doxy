package com.alexc.doxy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchFriendsFragment extends Fragment implements UserAddFriendAdapter.OnUserClickListener {

    private RecyclerView recyclerViewSearch;
    private UserAddFriendAdapter searchAdapter;
    private List<User> searchResultsList;
    private DatabaseHelper databaseHelper;

    // Otros métodos y lógica del fragmento

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_friends, container, false);

        databaseHelper = new DatabaseHelper(requireContext());

        // Configurar el SearchView
        SearchView searchViewFriends = view.findViewById(R.id.searchViewFriends);
        searchViewFriends.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Realizar la búsqueda cuando se pulsa el botón de búsqueda en el teclado
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Realizar la búsqueda mientras se va escribiendo en el SearchView
                performSearch(newText);
                return true;
            }
        });

        // Configurar el RecyclerView de resultados de búsqueda
        recyclerViewSearch = view.findViewById(R.id.recyclerViewSearchResults);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultsList = new ArrayList<>();
        searchAdapter = new UserAddFriendAdapter(searchResultsList, this);
        recyclerViewSearch.setAdapter(searchAdapter);

        return view;
    }

    private void performSearch(String query) {
        // Aquí debes implementar la lógica para buscar en la base de datos los usuarios cuyo username contenga el texto de 'query'
        // Luego, actualiza la lista 'searchResultsList' con los resultados obtenidos y notifica al adapter
        // Por ejemplo, si tienes un método llamado 'searchUsers' en tu databaseHelper, podrías hacer lo siguiente:
        searchResultsList.clear();
        searchResultsList.addAll(databaseHelper.searchUsers(query));
        searchAdapter.notifyDataSetChanged();
    }

    private void showAddFriendDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Agregar amigo");
        builder.setMessage("¿Deseas agregar a " + user.getUsername() + " como amigo?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("DoxyPrefs", Context.MODE_PRIVATE);
                String stringUserId = sharedPreferences.getString("userId", "");
                int currentUserId = Integer.parseInt(stringUserId);

                // Verificar si el ID del usuario es el mismo que el ID del usuario actual
                if (user.getId() == currentUserId) {
                    Toast.makeText(requireContext(), "Error: No puedes añadirte a ti mismo como amigo", Toast.LENGTH_SHORT).show();
                } else {
                    databaseHelper.addFriend(user.getId(), currentUserId);
                    FriendsListFragment friendsListFragment = (FriendsListFragment) getParentFragmentManager().findFragmentByTag("FriendsListFragment");
                    if (friendsListFragment != null) {
                        friendsListFragment.addFriend(user);
                    }
                    Toast.makeText(requireContext(), "Usuario agregado como amigo", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // No hacer nada, simplemente cerrar el cuadro de diálogo
            }
        });
        builder.create().show();
    }


    @Override
    public void onUserClick(User user) {
        showAddFriendDialog(user);
    }
}
