package se.kth.id2216.trivially;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {

    public String[][] scoreList;
    Activity activity;

    public ListViewAdapter(Activity activity, String[][] scoreList) {
        super();
        this.activity = activity;
        this.scoreList = scoreList;
    }

    @Override
    public int getCount() {
        return scoreList.length;
    }

    @Override
    public Object getItem(int position) {
        return scoreList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView name;
        TextView gamesPlayed;
        TextView successRate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_row, null);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.nameColumn);
            holder.gamesPlayed = convertView.findViewById(R.id.gamesPlayedColumn);
            holder.successRate = convertView
                    .findViewById(R.id.successRateColumn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String[] item = scoreList[position];
        holder.name.setText(item[0]);
        holder.gamesPlayed.setText(item[1]);
        holder.successRate.setText(item[2]);

        return convertView;
    }
}
