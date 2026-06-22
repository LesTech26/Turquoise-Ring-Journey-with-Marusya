package com.example.trjwm.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MapPointRepository {
    private static final List<MapPoint> POINTS = buildPoints();

    private MapPointRepository() {
    }

    public static List<MapPoint> getPoints() {
        return POINTS;
    }

    private static List<MapPoint> buildPoints() {
        List<MapPoint> points = new ArrayList<>();
        points.add(p("bolkhov", "Болхов", "Административный центр Болховского района", 53.4437, 36.0055, "district"));
        points.add(p("verkhov", "Верховье", "Железнодорожный центр района", 52.8220, 36.2440, "district"));
        points.add(p("glazunov", "Глазуновка", "Самый компактный район области", 52.4920, 36.3260, "district"));
        points.add(p("dmitrov", "Дмитровск", "Лесной юго-запад области", 52.5070, 35.1430, "district"));
        points.add(p("dolzhansk", "Долгое", "Юго-восточный районный центр", 52.1450, 37.4780, "district"));
        points.add(p("zalego", "Залегощь", "Центр района на Неручи и Зуше", 52.9000, 36.8900, "district"));
        points.add(p("znamensky", "Знаменское", "Северо-запад области", 53.0140, 35.9960, "district"));
        points.add(p("kolpny", "Колпна", "Аграрный центр района", 52.5240, 37.0340, "district"));
        points.add(p("korsakov", "Корсаково", "Северо-восточная окраина области", 52.6630, 36.1980, "district"));
        points.add(p("krasnoz", "Красная Заря", "Восточный районный центр", 52.7780, 37.7020, "district"));
        points.add(p("kromskoy", "Кромы", "Центральный узел дорог", 52.6870, 35.7730, "district"));
        points.add(p("livensky", "Ливны", "Крупный город, центр Ливенского района", 52.4280, 37.6090, "district"));
        points.add(p("maloarh", "Малоархангельск", "Южный районный центр", 52.4210, 36.5010, "district"));
        points.add(p("mcensk", "Мценск", "Северный центр района", 53.2820, 36.5750, "district"));
        points.add(p("novoderev", "Хомутово", "Центр Новодеревеньковского района", 52.8500, 36.7400, "district"));
        points.add(p("novosil", "Новосиль", "Древний районный центр", 52.9750, 37.0450, "district"));
        points.add(p("orlovsky", "Орёл", "Центр области и Орловского района", 52.9700, 36.0630, "district"));
        points.add(p("pokrov", "Покровское", "Юго-восточный районный центр", 52.8590, 36.8740, "district"));
        points.add(p("sverdlov", "Змиёвка", "Центр Свердловского района", 52.6710, 36.6980, "district"));
        points.add(p("soskov", "Сосково", "Западный районный центр", 52.7520, 35.7760, "district"));
        points.add(p("trosna", "Тросна", "Южный районный центр", 53.0270, 35.7920, "district"));
        points.add(p("urick", "Нарышкино", "Центр Урицкого района", 52.9750, 35.7250, "district"));
        points.add(p("hotynets", "Хотынец", "Западный район у Орловского Полесья", 53.1270, 35.3990, "district"));
        points.add(p("shablykino", "Шаблыкино", "Западный районный центр", 52.8570, 35.2030, "district"));

        return Collections.unmodifiableList(points);
    }

    private static MapPoint p(String districtId, String title, String description,
                              double latitude, double longitude, String pointType) {
        return new MapPoint(districtId, title, description, latitude, longitude, pointType);
    }
}
