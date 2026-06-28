package com.example.biruse_kolco.util;

import android.content.Context;

public final class ImageAssets {
    private ImageAssets() {
    }

    public static int drawableId(Context context, String resourceName) {
        if (resourceName == null || resourceName.trim().isEmpty()) {
            return 0;
        }
        return context.getResources().getIdentifier(
                resourceName,
                "drawable",
                context.getPackageName()
        );
    }

    public static String coatImageNameForDistrict(String districtId) {
        switch (districtId) {
            case "bolkhov":
                return "img_coa_bolkhov";
            case "dmitrov":
                return "img_coa_dmitrovsk";
            case "mcensk":
                return "img_coa_mcensk";
            case "trosna":
                return "img_coa_trosna";
            case "hotynets":
                return "img_coa_hotynets";
            default:
                return null;
        }
    }

    // ============================================================
    // ДЛЯ ФОТО В ГАЛЕРЕЕ
    // ============================================================
    public static String photoImageName(String photoName) {
        if (photoName == null) return null;
        switch (photoName) {
            case "Хотынец":
                return "img_hotynets";
            case "Орловское Полесье":
                return "img_orlovskoe_polese";
            case "Лесная тропа":
                return "img_lesnaya_tropa";
            case "Болхов":
                return "img_bolkhov";
            case "Кривцовский мемориал":
                return "img_krivcovskiy_memorial";
            case "Река Ока":
                return "img_reka_oka";
            case "Мценск":
                return "img_mcensk";
            case "Сельский пейзаж":
                return "img_selskiy_peyzazh";
            case "Историческая улица":
                return "img_istoricheskaya_ulitsa";
            case "Дмитровск":
                return "img_dmitrovsk";
            case "Лесной массив":
                return "img_lesnoy_massiv";
            case "Сельская дорога":
                return "img_selskaya_doroga";
            case "Тросна":
                return "img_trosna";
            case "Река Тросна":
                return "img_reka_trosna";
            case "Свято-Успенский храм":
                return "img_svytou_spenskiy_hram";
            default:
                return "img_" + photoName.toLowerCase()
                        .replace(" ", "_")
                        .replace("ё", "e")
                        .replace("район", "")
                        .trim();
        }
    }
}
