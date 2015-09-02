/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   20.08.15
 */
package com.bookmate.libs.demo.traits;

import com.bookmate.libs.demo.traits.readercode.SelectionStart;
import com.bookmate.libs.traits.Bus;

public class SelectionTraitHelper_ {
    private final Bus.EventListener<SelectionStart> selectionStartEventListener;

    SelectionTraitHelper_(final SelectionTrait selectionTrait) {
         selectionStartEventListener = new Bus.EventListener<SelectionStart>() {
            @Override
            public void onEvent(SelectionStart event) {
                selectionTrait.onSelectionStart(event);
            }
        };
        selectionTrait.bus.register(SelectionStart.class, selectionStartEventListener); // cur unregister
    }
}
