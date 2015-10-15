package com.bookmate.libs.epub;

/**
 * Created by defuera on 19/06/14.
 */
//A Reading System should have the ability to, at user selection, provide access to the NCX navMap in
//a fashion that allows the user to activate the links provided in the navMap, thus relocating
//the application's current reading position to the destination described by the selected NCX navPoint.
//The behavioral expectations described above apply to the NCX pageList and navList as well,
//if the given NCX contains said elements.

/**
 * this is an entry in the NCX file, so it is an item in the table of contents
 */
public class NavPoint {
    private final String id;
    //    private final int number;
    private final String title;
    private final String content;
    private final int nestingDepth;

    public NavPoint(String id, String title, String content, int nestingLevel) {
        this.id = id;
        this.nestingDepth = nestingLevel;
        this.title = title;
        this.content = content;
    }

    public final String getTitle() {
        return title;
    }

    public final String getContent() {
        return content;
    }

    public int getNestingDepth() {
        return nestingDepth;
    }


    public String toString() {
        return id + "_" + title + "_" + nestingDepth;
    }

    public String getId() {
        return id;
    }
}
