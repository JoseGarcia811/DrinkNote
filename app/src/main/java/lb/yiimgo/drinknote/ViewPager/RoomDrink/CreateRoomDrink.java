package lb.yiimgo.drinknote.ViewPager.RoomDrink;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import lb.yiimgo.drinknote.Entity.ConecctionSQLiteHelper;
import lb.yiimgo.drinknote.R;
import lb.yiimgo.drinknote.Utility.Utility;


public class CreateRoomDrink extends AppCompatActivity  {

    private EditText name;
    private EditText roomNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_create_rooms);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name = (EditText) findViewById(R.id.room_name);
        roomNumber = (EditText) findViewById(R.id.numberRoom);

        final Button button = findViewById(R.id.send_data_room);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                regiterDrinkRoom();
            }
        });
    }


    private void regiterDrinkRoom()
    {
        ConecctionSQLiteHelper conn = new ConecctionSQLiteHelper(this, "db_drinknote",null,1);
        SQLiteDatabase db = conn.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Utility.FIELD_NAME_ROOM , name.getText().toString());
        values.put(Utility.FIELD_ROOM_DRINK_UBICATION, roomNumber.getText().toString());

         db.insert(Utility.TABLE_ROOM_DRINK,Utility.FIELD_ID_ROOM,values);
        //Toast.makeText(getApplicationContext(),"id result: "+ idResult, Toast.LENGTH_SHORT).show();
        db.close();
    }

}