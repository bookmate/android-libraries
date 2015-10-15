package com.bookmate.libs.epub;

import java.util.HashMap;

/**
 * Created by khmelev on 04.05.14.
 * <p/>
 * Метаинформация о EPUB-файле документа
 * <p/>
 * container — string, содержание файла описания контейнера файла (для EPUB это OPF-контейнеры, так что тут важно только относительное месторасположение контейнера)
 * opf — string, содержание файла контейнера EPUB-файла
 * ncx — string, содержание NCX-файла (оглавление)
 * sizes — hash, соответсвие «имя файла» -> «размер файла в байтах»
 */
@SuppressWarnings("UnusedDeclaration")
public class DocumentMetadata {
    public byte[] container;
    public byte[] opf;
    public byte[] ncx;
    public HashMap<String, Long> sizes;
}
