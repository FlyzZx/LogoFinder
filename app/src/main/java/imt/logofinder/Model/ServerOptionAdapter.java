package imt.logofinder.Model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import imt.logofinder.R;

/**
 * Created by Tom on 18/02/2018.
 */

public class ServerOptionAdapter extends BaseAdapter implements SpinnerAdapter {

    private Context context = null;
    private List<ServerOptions> _servServerOptions = null;

    public ServerOptionAdapter(Context context,List<ServerOptions> servServerOptions) {
        this.context = context;
        this._servServerOptions = servServerOptions;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View vue = convertView;
        if(vue==null){
            LayoutInflater layout;
            layout= LayoutInflater.from(context);
            vue = layout.inflate(R.layout.layout_dropdown_server_options,null);
        }

        ServerOptions s = (ServerOptions) getItem(position);
        if(s != null){
            TextView serverName = (TextView) vue.findViewById(R.id.textView_serverName);

            TextView serverPath = (TextView) vue.findViewById(R.id.textView_serverPath);

            if(serverName != null && serverPath != null){
                serverName.setText(s.getServerName());
                serverPath.setText(s.getServerPath());
            }



        }
        return vue;
    }

    @Override
    public int getCount() {
        return this._servServerOptions.size();
    }

    @Override
    public Object getItem(int i) {
        return this._servServerOptions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


}
