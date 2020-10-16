package pro.fossa.apps.timer;

import java.util.Comparator;

public class ItemComparator implements Comparator<ItemData> {

	@Override
    public int compare(ItemData sp1, ItemData sp2) {
        return (sp1.id > sp2.id ) ? -1: (sp1.id < sp2.id) ? 1:0 ;
    }
}