/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   22.05.15
 */
package com.bookmate.libs.translateview;

//class TranslateLanguagesHelper {
//    private static final String PAIR_SEPARATOR = "-";
//
//    BiMap<String, String> availableLanguages; // en->English etc
//    Set<String> availableDirections;
//    String toLangCode;
//    /**
//     * 'from' codes logic is simple: when autodetect is selected, we pass autodect value to api call, then retreive the actual language from api and display it to user. But all subsequent api calls still need 'autodetect' as a paremeter, so we maintain two fields for this. But when user manually changes the 'from' language we remember his choice
//     */
//    private String fromLangCodeForDisplay, fromLangCodeForApiCall;
//    /**
//     * needed when we tried to translate in unsupported direction, so we retry with "autodetect"-"en"
//     */
//    private boolean forceDefaultTranslateDirection;
//    /**
//     * needed because Yandex doesn't support all the possible locales as ui languages
//     */
//    private boolean forceDefaultUiLanguage;
//
//    public boolean initialized() {
//        return availableLanguages == null;
//    }
//
//    public String toLanguageName() {
//        return languageName(toLangCode);
//    }
//
//    public String fromLanguageName() {
//        return languageName(fromLangCode());
//    }
//
//    /**
//     * @param langCode if null, returns "Autodetect"
//     */
//    public String languageName(String langCode) {
//        return TextUtils.isEmpty(langCode) ? bookmate.getString(R.string.autodetect) : (availableLanguages == null ? "" : availableLanguages.get(langCode));
//    }
//
//    public String langCode(String languageName) {
//        return availableLanguages.inverse().get(languageName);
//    }
//
//    /// lang lists
//
//    public List<String> fromLanguagesList() {
//        final BiMap<String, String> fromLanguages = Maps.filterKeys(availableLanguages, new Predicate<String>() { // now we need to select only the languages we can translate from
//            @Override
//            public boolean apply(String input) {
//                return Iterables.tryFind(availableDirections, Predicates.containsPattern(input + PAIR_SEPARATOR)).isPresent(); // if input is 'en' try to find 'en-' in directions
//            }
//        });
//        final List<String> languages = Ordering.natural().sortedCopy(fromLanguages.values());
//        languages.add(0, bookmate.getString(R.string.autodetect));
//        return languages;
//    }
//
//    public List<String> toLanguagesList() {
//        final BiMap<String, String> toLanguages;
//        if (TextUtils.isEmpty(fromLangCode())) { // finding all the possible translate directions
//            toLanguages = Maps.filterValues(availableLanguages, new Predicate<String>() { // now we need to select only the languages we can translate from
//                @Override
//                public boolean apply(String input) {
//                    return Iterables.tryFind(availableDirections, Predicates.containsPattern(PAIR_SEPARATOR + input)).isPresent(); // if input is 'en' try to find 'en-' in directions
//                }
//            });
//        } else { // finding all the languages we can translate to from current language
//            Collection<String> directionsFromSelectedLang = Collections2.filter(availableDirections, Predicates.containsPattern(fromLangCode() + PAIR_SEPARATOR));
//            directionsFromSelectedLang = Collections2.transform(directionsFromSelectedLang, new Function<String, String>() {
//                @Override
//                public String apply(String input) {
//                    return input.substring(input.indexOf(PAIR_SEPARATOR) + 1); // removing "en-" from "en-ru"
//                }
//            });
//            toLanguages = Maps.filterKeys(availableLanguages, Predicates.in(directionsFromSelectedLang));
//        }
//        return Ordering.natural().immutableSortedCopy(toLanguages.values());
//    }
//
//    /// init
//
//    public boolean initializeFromCache() {
//        if (availableLanguages != null && !TextUtils.isEmpty(bookmate.prefs.availableTranslateLanguages().get())) {
//            final Map<String, String> stringMap = bookmate.gson.fromJson(bookmate.prefs.availableTranslateLanguages().get(), new TypeToken<HashMap<String, String>>() {
//            }.getType());
//            availableLanguages = HashBiMap.create(stringMap); // if I instead pass HashBiMap into TypeToken above, it crashes
//            availableDirections = bookmate.prefs.availableTranslateDirections().get();
//        }
//        return availableLanguages != null && availableDirections != null;
//    }
//
//    public void initialize(Map<String, String> langs, String[] dirs) {
//        availableLanguages = HashBiMap.create(langs);
//        availableDirections = new HashSet<>(Arrays.asList(dirs));
//        bookmate.prefs.availableTranslateLanguages().put(bookmate.gson.toJson(availableLanguages));
//        bookmate.prefs.availableTranslateDirections().put(availableDirections);
//    }
//
//    /// language pair
//
//    /**
//     * @return returns code of from language as displayed to user
//     */
//    public String fromLangCode() {
//        return fromLangCodeForDisplay;
//    }
//
//    public void setFromLanguage(String langCode, boolean displayingValueOnly) {
//        fromLangCodeForDisplay = langCode;
//        if (!displayingValueOnly)
//            fromLangCodeForApiCall = langCode;
//    }
//
//    public String extractFromLanguage(String langPair) {
//        return langPair.substring(0, langPair.indexOf(PAIR_SEPARATOR));
//    }
//
//    /**
//     * @return en-ru etc
//     */
//    String getTranslateLanguagesPair() {
//        if (forceDefaultTranslateDirection) {
//            setDefaultTranslateDirection();
//            forceDefaultTranslateDirection = false;
//        } else if (TextUtils.isEmpty(toLangCode)) {
//            String translatePair = bookmate.prefs.translatePair().get();
//
//            if (TextUtils.isEmpty(translatePair)) {
//                toLangCode = Locale.getDefault().getLanguage(); // trying to guess the language to translate to
//                setFromLanguage("", false);
//                if (TextUtils.isEmpty(toLangCode)) {
//                    setDefaultTranslateDirection();
//                }
//            } else {
//                int dashIndex = translatePair.indexOf(PAIR_SEPARATOR);
//                String langCode = dashIndex > 0 ? translatePair.substring(0, dashIndex) : "";
//                setFromLanguage(langCode, false);
//                toLangCode = translatePair.substring(dashIndex + 1, translatePair.length());
//            }
//        }
//
//        final String translateLangPair = TextUtils.isEmpty(fromLangCodeForApiCall) ? toLangCode : fromLangCodeForApiCall + PAIR_SEPARATOR + toLangCode;
//        bookmate.prefs.translatePair().put(translateLangPair);
//        return translateLangPair;
//    }
//
//    /**
//     * used by yandex to determine in which language to generate languages list etc
//     */
//    String getUiLanguage() {
//        return forceDefaultUiLanguage ? "en" : Locale.getDefault().getLanguage();
//    }
//
//    private void setDefaultTranslateDirection() {
//        toLangCode = "en";
//        setFromLanguage("", !forceDefaultTranslateDirection); // if flag is set, this method is called after we failed to translate with current lang-pair, so we change it
//    }
//
//    public void forceDefaultTranslateDirection() {
//        this.forceDefaultTranslateDirection = true;
//    }
//
//    public void forceDefaultUiLanguage() {
//        this.forceDefaultUiLanguage = true;
//    }
//}
