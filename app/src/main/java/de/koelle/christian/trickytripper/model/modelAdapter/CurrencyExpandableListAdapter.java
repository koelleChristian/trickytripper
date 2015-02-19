package de.koelle.christian.trickytripper.model.modelAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.model.CurrencyWithName;

public class CurrencyExpandableListAdapter extends BaseExpandableListAdapter {

    private final Integer[] visualToModelMapping;
    private final int size;

    private static final String NAME = "NAME";

    private final List<List<CurrencyWithName>> currencies;
    private final LayoutInflater inflater;

    private final String[] mChildFrom = new String[] { NAME };
    private final int[] mChildTo = new int[] { android.R.id.text1 };
    private final String[] mGroupFrom = mChildFrom;
    private final int[] mGroupTo = mChildTo;

    private final CurrencyGroupNamingCallback callback;

    public CurrencyExpandableListAdapter(Context context, List<List<CurrencyWithName>> currencies,
            CurrencyGroupNamingCallback callback) {
        super();
        this.currencies = currencies;
        this.callback = callback;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        List<Integer> listsMap = new ArrayList<>();

        for (int i = 0; i < currencies.size(); i++) {
            if (!currencies.get(i).isEmpty()) {
                listsMap.add(i);
            }
        }
        size = listsMap.size();
        visualToModelMapping = new Integer[size];
        listsMap.toArray(visualToModelMapping);
    }

    public int getGroupCount() {
        return size;
    }

    public int getChildrenCount(int groupPosition) {
        return getListById(groupPosition).size();
    }

    private int getVisualToModelIndex(int groupPosition) {
        return visualToModelMapping[groupPosition];
    }

    public CurrencyWithName getRecordByVisualId(int groupPosition, int childPosition) {
        return (CurrencyWithName) getChildValueByPosition(groupPosition, childPosition);
    }

    private List<CurrencyWithName> getListById(int groupPosition) {
        return currencies.get(getVisualToModelIndex(groupPosition));

    }

    public Object getGroup(int groupPosition) {
        HashMap<String, Object> result = new HashMap<>();
        result.put(NAME, getGroupValueByPosition(getVisualToModelIndex(groupPosition)));
        return result;
    }

    public Object getChild(int groupPosition, int childPosition) {
        HashMap<String, Object> result = new HashMap<>();
        result.put(NAME, getChildValueByPosition(groupPosition, childPosition));
        return result;
    }

    private Object getGroupValueByPosition(int modelGroupPosition) {
        return callback.getGroupDescription(modelGroupPosition);
    }

    private Object getChildValueByPosition(int groupPosition, int childPosition) {
        List<CurrencyWithName> listById = getListById(groupPosition);
        return listById.get(childPosition);
    }

    @SuppressWarnings("unchecked")
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = newGroupView(isExpanded, parent);
        }
        else {
            v = convertView;
        }
        bindView(v, (Map<String, ?>) getGroup(groupPosition), mGroupFrom, mGroupTo);
        return v;
    }

    @SuppressWarnings("unchecked")
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
            ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = newChildView(isLastChild, parent);
        }
        else {
            v = convertView;
        }
        bindView(v, (Map<String, ?>) getChild(groupPosition, childPosition), mChildFrom, mChildTo);
        return v;
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public View newChildView(boolean isLastChild, ViewGroup parent) {
        int mChildLayout = R.layout.simple_expandable_list_item_2;
        int mLastChildLayout = R.layout.simple_expandable_list_item_2;
        return inflater.inflate((isLastChild) ? mLastChildLayout : mChildLayout, parent, false);
    }

    /**
     * Instantiates a new View for a group.
     * 
     * @param isExpanded
     *            Whether the group is currently expanded.
     * @param parent
     *            The eventual parent of this new View.
     * @return A new group View
     */
    public View newGroupView(boolean isExpanded, ViewGroup parent) {
        int mCollapsedGroupLayout = R.layout.simple_expandable_list_item_1;
        int mExpandedGroupLayout = R.layout.simple_expandable_list_item_1;
        return inflater.inflate((isExpanded) ? mExpandedGroupLayout : mCollapsedGroupLayout,
                parent, false);
    }

    private void bindView(View view, Map<String, ?> data, String[] from, int[] to) {
        int len = to.length;

        for (int i = 0; i < len; i++) {
            TextView v = (TextView) view.findViewById(to[i]);
            if (v != null) {
                v.setText(data.get(from[i]) + "");
            }
        }
    }

}
