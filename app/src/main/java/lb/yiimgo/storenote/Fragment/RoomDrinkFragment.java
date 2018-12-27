package lb.yiimgo.storenote.Fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.SearchView;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import lb.yiimgo.storenote.Entity.RoomDrinks;
import lb.yiimgo.storenote.Entity.VolleySingleton;
import lb.yiimgo.storenote.R;
import lb.yiimgo.storenote.Utility.SessionManager;
import lb.yiimgo.storenote.ViewPager.RoomDrink.RoomDrinkAdapter;

public class RoomDrinkFragment extends Fragment implements Response.Listener<JSONObject>,
        Response.ErrorListener
{
    public View view;
    public RecyclerView recyclerRoomDrink;
    public ArrayList<RoomDrinks> listRoomDrink;
    public ArrayList<RoomDrinks> newList;
    public ProgressDialog progressDialog;
    public RoomDrinkAdapter adapter;
    public RoomDrinks RoomDrink = null;
    public RequestQueue requestQueue;
    public JsonObjectRequest jsonObjectRequest;
    public SearchView searchView;
    public SessionManager sessionManager;
    public TextView notFound;
    public boolean ifSearch = false;

    public RoomDrinkFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_room_drink, container, false);
        listRoomDrink = new ArrayList<>();

        recyclerRoomDrink = (RecyclerView) view.findViewById(R.id.display_room);
        recyclerRoomDrink.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerRoomDrink.setHasFixedSize(true);
        notFound = (TextView) view.findViewById(R.id.not_found);
        adapterOnClick();
        requestQueue = Volley.newRequestQueue(getContext());

        loadWebServices();

        return view;
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(),"Error " + error.toString(),Toast.LENGTH_LONG).show();
        progressDialog.hide();
    }
    @Override
    public void onResponse(JSONObject response) {

        JSONArray json = response.optJSONArray("roomDrink");
        try{
            for(int i =0; i<json.length(); i++)
            {
                RoomDrink = new RoomDrinks();
                JSONObject jsonObject = null;
                jsonObject =json.getJSONObject(i);

                RoomDrink.setIdRoom(jsonObject.optString("IdRoom"));
                RoomDrink.setWaiterRoom(jsonObject.getString("WaiterRoom"));
                RoomDrink.setRoomUbication("Ubication - " + jsonObject.optString("RoomUbication"));
                RoomDrink.setStatus(jsonObject.optString("Status"));

                listRoomDrink.add(RoomDrink);

            }
        }catch (JSONException e)
        {
            e.printStackTrace();
        }

        progressDialog.hide();

        recyclerRoomDrink.setAdapter(adapter);

    }

    public void adapterOnClick()
    {
        adapter = new RoomDrinkAdapter(getActivity(), listRoomDrink, new RoomDrinkAdapter.ListAdapterListener() {

            @Override
            public void onClickAddButton(View v) {
                addDialog(v);

            }
        });
    }
    public void addDialog(View v)
    {
        String value;
        if(ifSearch)
            value = newList.get(recyclerRoomDrink.getChildAdapterPosition(v)).getRoomUbication();
        else
            value = listRoomDrink.get(recyclerRoomDrink.getChildAdapterPosition(v)).getRoomUbication();

        Toast.makeText(getActivity(), "Popup ID: " + value, Toast.LENGTH_SHORT).show();
    }
    public void loadWebServices()
    {
        sessionManager = new SessionManager(getContext());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando...");
        progressDialog.show();
        String idUser = sessionManager.getDataFromSession().get(4);
        String idProfile = sessionManager.getDataFromSession().get(0);

        String url = "http://rizikyasociados.com.do/wsDrinkNote/Main/getDataRooms?Id=" + idUser +"&idProfile="+idProfile;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        requestQueue.add(jsonObjectRequest);
    }

    private void webServiceDelete(String id,final int po) {

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        StringRequest stringRequest;
        String url="http://rizikyasociados.com.do/wsDrinkNote/Main/deleteRoomDrink?Id="+id;

        stringRequest =new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.hide();
                listRoomDrink.remove(po);
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Not connection",Toast.LENGTH_SHORT).show();
                progressDialog.hide();
            }
        });
        VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(stringRequest);
    }
    public void alertDialog(final int position, final String method)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage("Are your sure "+method+" this item?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        switch (method)
                        {
                            case  "delete" :
                                webServiceDelete(listRoomDrink.get(position).getIdRoom().toString(),position);
                                break;
                        }
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void refresh()
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
        ifSearch = false;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onPrepareOptionsMenu(menu);
        inflater.inflate(R.menu.menu_room_fragment, menu);
        MenuItem item = menu.findItem(R.id.search);
        searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(listRoomDrink.size() > 0){
                    ifSearch = true;
                    newText = newText.toLowerCase();
                    newList = new ArrayList<>();

                    for(RoomDrinks c : listRoomDrink)
                    {
                        String name = c.getRoomUbication().toLowerCase();
                        if(name.contains(newText)){
                            newList.add(c);
                        }
                    }

                    if (newList.size() == 0){
                        if(!newText.isEmpty())
                            notFound.setText("Record not found with '"+newText+"'");
                    }

                    adapter.updateList(newList);

                    return true;
                }else{
                    return false;
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.refresh:

                refresh();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
