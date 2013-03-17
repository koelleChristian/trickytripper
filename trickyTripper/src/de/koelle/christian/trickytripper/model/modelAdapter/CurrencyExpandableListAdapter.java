package de.koelle.christian.trickytripper.model.modelAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import de.koelle.christian.trickytripper.model.CurrencyWithName;

public class CurrencyExpandableListAdapter extends BaseExpandableListAdapter {

    private static final int GROUP_POS_ID_MATCHING = 0;
    private static final int GROUP_POS_ID_USED = 1;
    private static final int GROUP_POS_ID_PROJECT = 2;
    private static final int GROUP_POS_ID_ELSE = 3;

    private final Integer[] visualToModelMapping;
    private final int size;

    private static final String NAME = "NAME";

    private final List<List<CurrencyWithName>> currencies;
    private final LayoutInflater inflater;

    private final int mCollapsedGroupLayout = android.R.layout.simple_expandable_list_item_1;
    private final int mExpandedGroupLayout = android.R.layout.simple_expandable_list_item_1;
    private final int mChildLayout = android.R.layout.simple_expandable_list_item_2;
    private final int mLastChildLayout = android.R.layout.simple_expandable_list_item_2;

    private final String[] mChildFrom = new String[] { NAME };
    private final int[] mChildTo = new int[] { android.R.id.text1 };
    private final String[] mGroupFrom = mChildFrom;
    private final int[] mGroupTo = mChildTo;

    public CurrencyExpandableListAdapter(Context context, List<List<CurrencyWithName>> currencies) {
        super();
        this.currencies = currencies;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        List<Integer> listsMap = new ArrayList<Integer>();

        for (int i = 0; i < currencies.size(); i++) {
            if (!currencies.get(i).isEmpty()) {
                listsMap.add(Integer.valueOf(i));
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
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put(NAME, getGroupValueByPosition(getVisualToModelIndex(groupPosition)));
        return result;
    }

    public Object getChild(int groupPosition, int childPosition) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put(NAME, getChildValueByPosition(groupPosition, childPosition));
        return result;
    }

    private Object getGroupValueByPosition(int modelGroupPosition) {
        if (modelGroupPosition == GROUP_POS_ID_MATCHING) {
            return "Zuletzt benutzt.";
        }
        else if (modelGroupPosition == GROUP_POS_ID_USED) {
            return "In rates";
        }
        else if (modelGroupPosition == GROUP_POS_ID_PROJECT) {
            return "Im Projekt";
        }
        else if (modelGroupPosition == GROUP_POS_ID_ELSE) {
            return "Sonstige";
        }
        else {
            throw new RuntimeException("Not supported");
        }
    }

    private Object getChildValueByPosition(int groupPosition, int childPosition) {
        List<CurrencyWithName> listById = getListById(groupPosition);
        return listById.get(childPosition);
    }

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
