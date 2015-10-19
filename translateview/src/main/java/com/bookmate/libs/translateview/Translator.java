/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   29.04.15
 */
package com.bookmate.libs.translateview;

//@EBean
//public class Translator extends ReaderSubmodule {
//    @SuppressWarnings("unused")
//    private static final String LOG_TAG = Translator.class.getSimpleName();
//
//    @App
//    Bookmate bookmate;
//
//    @ViewById
//    ReaderPopup readerPopup;
//
//    @ViewById
//    TextView textToTranslate;
//
//    @ViewById
//    TextView translateTo, translateFrom, translationResult, dictionaryResult;
//
//    @ViewById
//    View translateResultContainer, translateResultInnerContainer, translateUiContainer, translateEditOk;
//
//    @ViewById
//    LoaderView translateInnerLoader;
//
//    @Inject
//    Bus bus;
//
//    @Bean
//    TranslateLanguagesHelper languagesHelper;
//
//    private YandexTranslateAPI yandexTranslateAPI;
//    private YandexDictionaryAPI yandexDictionaryAPI;
//    private ArrayAdapter<String> fromLanguagesAdapter;
//    private ArrayAdapter<String> toLanguagesAdapter;
//    /**
//     * needed to store cfi etc, to pass later to markersAPI
//     */
//    private TextSelected selectedText;
//    private boolean isUiVisible;
//
//    @AfterViews
//    void ready() {
//        yandexTranslateAPI = createApiBuilder(YandexTranslateAPI.API_KEY, YandexTranslateAPI.ENDPOINT).create(YandexTranslateAPI.class);
//        yandexDictionaryAPI = createApiBuilder(YandexDictionaryAPI.API_KEY, YandexDictionaryAPI.ENDPOINT).create(YandexDictionaryAPI.class);
//        textToTranslate.setMaxHeight(readerPopup.getMaxPopupHeight() / 4); // otherwise if the text is big it's occupying too much space, and too few is left to the translation
//        textToTranslate.setHorizontallyScrolling(false); // for some reason, setting this in xml doesn't work. Needed for making EditText support multi-lines. http://stackoverflow.com/a/17033570/190148
//        textToTranslate.setCustomSelectionActionModeCallback(Utils.noActionMode()); // for some reason "isTextSelectable" in xml doesn't work
//    }
//
//    private RestAdapter createApiBuilder(final String apiKey, String endPoint) {
//        return new RestAdapter.Builder()
//                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.BASIC)
//                .setRequestInterceptor(new RequestInterceptor() {
//                    @Override
//                    public void intercept(RequestFacade request) {
//                        request.addQueryParam("key", apiKey);
//                    }
//                })
//                .setConverter(new GsonConverter(bookmate.gson))
//                .setClient(bookmate.httpClient)
//                .setEndpoint(endPoint).build();
//    }
//
//    @UiThread
//    @Event
//    void onTranslatePressed(TextSelected event) {
//        selectedText = event;
//        textToTranslate.setText(event.text);
//        readerPopup.prepareTranslationView();
//        isUiVisible = true;
//        translate(); // we don't need to wait for languages list loading, we can just start translation
//        initLanguages();
//    }
//
//    @UiThread(propagation = UiThread.Propagation.REUSE)
//    void initLanguages() {
//        if (languagesHelper.initializeFromCache())
//            initLanguagesLists();
//        else
//            loadAvailableLanguages();
//    }
//
//
//    /// init languages lists section
//
//    private void initLanguagesLists() {
//        initFromLanguagesList();
//        initToLanguagesList();
//    }
//
//    private void initFromLanguagesList() {
//        if (languagesHelper.initialized())
//            return;
//
//        if (fromLanguagesAdapter == null)
//            fromLanguagesAdapter = createLanguagesAdapter(languagesHelper.fromLanguagesList());
//        translateFrom.setText(languagesHelper.fromLanguageName());
//    }
//
//    /**
//     * "To" languages list depends on the "from" language selected
//     */
//    private void initToLanguagesList() {
//        if (languagesHelper.initialized())
//            return;
//
//        Log.d(LOG_TAG, "initToLanguagesList from: " + languagesHelper.fromLangCode());
//        toLanguagesAdapter = createLanguagesAdapter(languagesHelper.toLanguagesList());
//        translateTo.setText(languagesHelper.toLanguageName());
//    }
//
//    @NonNull
//    private ArrayAdapter<String> createLanguagesAdapter(List<String> langugages) {
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.translate_language_item_view, langugages.toArray(new String[langugages.size()]));
//        arrayAdapter.setDropDownViewResource(R.layout.translate_language_item_view);
//        return arrayAdapter;
//    }
//
//
//    /// update languages list
//
//    /**
//     * when 'from' language is updated we also need to repopulate 'to' languages list
//     */
//    private void updateLanguage(String langCode, boolean fromLanguage, boolean userInitiated) {
//        Log.d(LOG_TAG, "updateLanguage lang: " + langCode + " from: " + fromLanguage);
//        if (fromLanguage) {
//            languagesHelper.setFromLanguage(langCode, !userInitiated);
//            initToLanguagesList();
//        } else
//            languagesHelper.toLangCode = langCode;
//
//        TextView textView = fromLanguage ? translateFrom : translateTo;
//        textView.setText(languagesHelper.languageName(langCode));
//    }
//
//    /// show languages
//
//    @Click
//    void swapLanguagesClicked() {
//        Log.d(LOG_TAG, "swapLanguagesClicked");
//
//        String tmp = languagesHelper.fromLangCode();
//        updateLanguage(languagesHelper.toLangCode, true, true);
//        updateLanguage(tmp, false, true);
//
//        translate();
//    }
//
//    @Click
//    void translateToClicked() {
//        showLanguagesPanel(toLanguagesAdapter);
//    }
//
//    @Click
//    void translateFromClicked() {
//        showLanguagesPanel(fromLanguagesAdapter);
//    }
//
//    private void showLanguagesPanel(final ArrayAdapter<String> languagesAdapter) {
//        new BottomSheet.Builder(getContext())
//                .background(R.drawable.bg_card_no_bottom)
//                .sheet(languagesAdapter)
//                .itemClickListener(new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        updateLanguage(languagesHelper.langCode(languagesAdapter.getItem(which)), languagesAdapter == fromLanguagesAdapter, true);
//                        translate();
//                    }
//                })
//                .setMinHeight(Utils.getScreenWidth(getContext()))
//                .show();
//    }
//
//    /// edit text
//
//    /**
//     * actually we need to detect when the soft keyboard is shown/hidden, but there is no way...
//     */
//    @FocusChange
//    void textToTranslateFocusChanged(boolean hasFocus) {
//        translateResultContainer.setVisibility(hasFocus ? View.INVISIBLE : View.VISIBLE);
//        if (!hasFocus) {
//            Utils.hideSoftKeyboard(getContext(), textToTranslate); // it doesn't always hide automatically
//            bus.event(new HideSystemUi()); // for some reason without this we still see the status bar
//        }
//    }
//
//    @TextChange
//    void textToTranslateTextChanged() {
//        translateEditOk.setVisibility(View.VISIBLE);
//    }
//
//    @EditorAction(R.id.text_to_translate)
//    void textToTranslateEditorAction() {
//        translate();
//    }
//
//    @Click
//    void translateEditOk() {
//        textToTranslate.clearFocus(); // hides the keyboard
//        translate();
//    }
//
//    @Click
//    void translateResultInnerContainerClicked() {
//        if (translateUiContainer.getVisibility() != View.VISIBLE)
//            translateUiContainer.setVisibility(View.VISIBLE);
//        else if (translateResultContainer.getHeight() < translateResultInnerContainer.getHeight())  // only need to maximize if the translation is bigger
//            translateUiContainer.setVisibility(View.GONE);
//    }
//
//
//    /// load languages
//
//    private void loadAvailableLanguages() {
//        Background.doTask(getContext(), YandexLanguagesResult.class, new Background.Task<YandexLanguagesResult>() {
//            @Override
//            public YandexLanguagesResult run() {
//                try {
//                    return yandexTranslateAPI.getLanguages(languagesHelper.getUiLanguage());
//                } catch (RetrofitError error) {
//                    onLoadError();
//                    return null;
//                }
//            }
//
//            @Override
//            public void onResult(YandexLanguagesResult result) {
//                if (result == null)
//                    return;
//                if (result.getLangs() == null) { // happens when yandex doesn't support device locale language
//                    languagesHelper.forceDefaultUiLanguage();
//                    loadAvailableLanguages();
//                } else {
//                    languagesHelper.initialize(result.getLangs(), result.getDirs());
//                    initLanguagesLists();
//                }
//            }
//        });
//    }
//
//    @Click
//    void saveNote() {
//        bus.event(new CreateMarker(selectedText.text, selectedText.startCfi, selectedText.endCfi, translationResult.getText().toString(), SelectionAction.quote())); // we don't use Action.NOTE, cauz it would show a dialog
//        readerPopup.hide();
//    }
//
//    /// translate section
//
//    private void translate() {
//        final String text = textToTranslate.getText().toString();
//        final String languagesPair = languagesHelper.getTranslateLanguagesPair();
//        translateInnerLoader.showLoading();
//        translateEditOk.setVisibility(View.GONE);
//
//        lookupDictionary(text, languagesPair);
//
//        Background.doTask(getContext(), YandexTranslateResult.class, new Background.Task<YandexTranslateResult>() {
//            @Override
//            public YandexTranslateResult run() {
//                try {
//                    return yandexTranslateAPI.translate(text, languagesPair);
//                } catch (RetrofitError e) {
//                    try {
//                        return bookmate.gson.fromJson(new String(((TypedByteArray) e.getResponse().getBody()).getBytes()), YandexTranslateResult.class); // in case of errors yandex returns code 400, so we get exception. This code returns the result and allows to get access to error code
//                    } catch (NullPointerException | JsonSyntaxException ignored) { // e.getResponse can be null if no internet
//                        onLoadError();
//                        return null;
//                    }
//                }
//            }
//
//            @Override
//            public void onResult(YandexTranslateResult result) {
//                if (result == null)
//                    return;
//                if (result.isSuccessfull()) {
//                    translateInnerLoader.hide();
//                    updateLanguage(languagesHelper.extractFromLanguage(result.getLang()), true, false);  // 'from' lang may have been autodetected. In this case we need to display correct 'from' lang, but we need to continue using 'autodetect' in future api calls
//
//                    if (result.getTranslation() != null) {
//                        textToTranslate.clearFocus(); // needed after text editing to show results view again
//                        translationResult.setText(result.getTranslation()[0]);
//                        readerPopup.hideLoader();
//                    }
//                } else if (result.code == YandexResult.RESULT_CODE_DIRECTION_NOT_SUPPORTED) { // usually happens on first launch if device uses some exotic locale a la Indonesian etc
//                    languagesHelper.forceDefaultTranslateDirection();
//                    translate(); // try to translate in default direction now
//                }
//            }
//        });
//    }
//
//    private void lookupDictionary(final String text, final String languagesPair) {
//        Background.doTask(getContext(), YandexDictionaryResult.class, new Background.Task<YandexDictionaryResult>() {
//            @Override
//            public YandexDictionaryResult run() {
//                try {
//                    return yandexDictionaryAPI.lookup(text, languagesPair, YandexDictionaryAPI.FLAGS_MORPHO, languagesHelper.getUiLanguage());
//                } catch (Exception e) { // dictionary is optional so we don't perform sophisticated error handling here
//                    return null;
//                }
//            }
//
//            @Override
//            public void onResult(YandexDictionaryResult result) {
//                if (result != null && result.isSuccessfull()) {
//                    dictionaryResult.setVisibility(View.VISIBLE);
//                    dictionaryResult.setText(Html.fromHtml(result.html()));
//                } else {
//                    dictionaryResult.setVisibility(View.GONE);
//                }
//            }
//        });
//    }
//
//    @UiThread(propagation = UiThread.Propagation.REUSE)
//    void onLoadError() {
//        if (readerPopup.isContentVisible()) {
//            translateInnerLoader.showNetworkError(new Exception());
//        } else if (isUiVisible) { // the error happened before popup was rendered
//            showToast(R.string.text_no_network);
//            readerPopup.hide();
//            isUiVisible = false;
//        }
//    }
//
//}
//// RDR  sort langs: english on top
