package org.rmiralles.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.rmiralles.app.R;
import org.rmiralles.app.base.Text;

import java.util.List;

public class Adapter extends ArrayAdapter<Text> {

    private List<Text> textList;
    private LayoutInflater inflater;
    private Context context;
    private int layoutId;

    public Adapter(Context context, int layoutId, List<Text> textsList) {
        super(context, layoutId, textsList);
        this.context = context;
        this.textList = textsList;
        this.layoutId = layoutId;
    }

    static class ItemText{
        TextView tvText;
        TextView tvBusinessTitle;
    }

    @Override
    public int getCount() {
        return textList.size();
    }

    @Override
    public Text getItem(int i) {
        return textList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ItemText itemText;

        if (view == null) {
            inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(layoutId, viewGroup, false);

            itemText = new ItemText();
            itemText.tvBusinessTitle = (TextView) view.findViewById(R.id.tvBusinessTitle);
            itemText.tvText = (TextView) view.findViewById(R.id.tvText);

            view.setTag(itemText);
        } else {
            itemText = (ItemText) view.getTag();
        }

        Text text = textList.get(position);
        itemText.tvBusinessTitle.setText(text.getBusiness_title());
        itemText.tvText.setText(text.getText());

        return view;
    }

}
