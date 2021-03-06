package es.ikergarciagutierrez.promul.concesionario.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.ikergarciagutierrez.promul.concesionario.R;
import es.ikergarciagutierrez.promul.concesionario.view.adapter.viewholder.CarViewHolder;

/**
 * Clase que define el Adapter del RecyclerView. Aquí se llenan cada uno de los items
 * con los datos de la BD
 */
public class CarAdapter extends RecyclerView.Adapter<CarViewHolder> implements View.OnClickListener {

    /**
     * Campos de la clase
     */
    private static final String URL = "jdbc:mysql://146.59.237.189:3306/dam208_iggconcesionario";
    private static final String USER = "dam208_igg";
    private static final String PASSWORD = "dam208_igg";

    private String title, description, reference, price, location, images,
            linkPage, fuel, km, transmission, color, power, numDoors, year;

    private String defaultImg = "https://images.assetsdelivery.com/compings_v2/pavelstasevich/pavelstasevich1811/pavelstasevich181101032.jpg";

    private ArrayList<String> carList = new ArrayList<>();
    private Context context;

    private View.OnClickListener listener;

    /**
     * Constructor pra el Adapter
     *
     * @param context
     */
    public CarAdapter(Context context) {
        this.context = context;
    }

    /**
     * Método que infla el layout con el ViewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        new InfoAsyncTask().execute();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car, parent, false);
        view.setOnClickListener(this);
        return new CarViewHolder(view);
    }

    /**
     * Método que da valores a los datos del ViewHolder
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {

        if (!carList.isEmpty()) {

                String carAdDetail = carList.get(position);
                String[] carAdDetails = carAdDetail.split("=");

                title = carAdDetails[0];
                price = carAdDetails[3];
                location = carAdDetails[4];
                fuel = carAdDetails[7];
                km = carAdDetails[8];
                year = carAdDetails[13];
                images = carAdDetails[5];

                holder.tvAdCarTitle.setText(title);
                holder.tvAdCarPrice.setText("Precio: " + price + " €");
                holder.etAdCarLocation.setText(location);
                holder.etAdCarFuel.setText(fuel);
                holder.etAdCarKm.setText(km);
                holder.etAdCarYear.setText(year);

            if (title.isEmpty()) {
                holder.tvAdCarTitle.setVisibility(View.GONE);
            }
            if (price.isEmpty()) {
                holder.tvAdCarPrice.setVisibility(View.GONE);
            }
            if (location.isEmpty()) {
                holder.etAdCarLocation.setVisibility(View.GONE);
                holder.tilAdCarLocation.setVisibility(View.GONE);
            }
            if (fuel.isEmpty()) {
                holder.etAdCarFuel.setVisibility(View.GONE);
                holder.tilAdCarFuel.setVisibility(View.GONE);
            }
            if (km.isEmpty()) {
                holder.etAdCarKm.setVisibility(View.GONE);
                holder.tilAdCarKm.setVisibility(View.GONE);
            }
            if (year.isEmpty()) {
                holder.etAdCarYear.setVisibility(View.GONE);
                holder.tilAdCarYear.setVisibility(View.GONE);
            }
            if (images.isEmpty()) {
                Picasso.get().load(defaultImg).into(holder.ivAdCar);
            } else {
                String[] img = images.split(";");
                Picasso.get().load(img[0]).into(holder.ivAdCar);
            }

        } else {
            showToast("Cargando anuncios...");
        }
    }

    /**
     * Método que devuelve el número de items que se mostrarán en el RecyclerView
     *
     * @return El número de items que se mostrarán en el RecyclerView
     */
    @Override
    public int getItemCount() {
        if (carList.isEmpty()) {
            return 5;
        }

        return carList.size();
    }

    /**
     * Método que establece el Listener de los items del RecyclerView
     *
     * @param listener
     */
    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.onClick(view);
        }
    }

    /**
     * Método que obtiene los datos de la BD y llena un ArrayList con ellos
     */
    @SuppressLint("StaticFieldLeak")
    public class InfoAsyncTask extends AsyncTask<Void, Void, Map<String, String>> {
        @Override
        protected Map<String, String> doInBackground(Void... voids) {
            Map<String, String> info = new HashMap<>();

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                String sql = "SELECT title, description, reference, price, location, images, linkPage," +
                        "fuel, km, transmission, color, power, numDoors, year FROM coches";
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();

                int numColumn = statement.getMetaData().getColumnCount();

                if (carList.size() < numColumn) {
                    while (resultSet.next()) {

                        carList.add(resultSet.getString("title") + "=" + resultSet.getString("description") + "=" + resultSet.getString("reference") + "="
                                + resultSet.getString("price") + "=" + resultSet.getString("location") + "=" + resultSet.getString("images") + "=" + resultSet.getString("linkPage")
                                + "=" + resultSet.getString("fuel") + "=" + resultSet.getString("km") + "=" + resultSet.getString("transmission") + "=" + resultSet.getString("color")
                                + "=" + resultSet.getString("power") + "=" + resultSet.getString("numDoors") + "=" + resultSet.getString("year"));

                    }
                }

            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

            return info;
        }

        @Override
        protected void onPostExecute(Map<String, String> result) {

        }
    }

    /**
     * Método que muestra un Toast personalizado
     *
     * @param message Mensaje que queremos que aparezca en el Toast
     */
    private void showToast(String message) {
        Toast toast = new Toast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
        TextView tvToast = view.findViewById(R.id.tvMessage);
        tvToast.setText(message);
        toast.setView(view);
        toast.show();
    }

}
