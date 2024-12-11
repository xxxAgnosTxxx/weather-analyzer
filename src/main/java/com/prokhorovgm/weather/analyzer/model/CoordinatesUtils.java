package com.prokhorovgm.weather.analyzer.model;

import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
@Getter
public class CoordinatesUtils {
    public final Map<String, double[]> regionCoordinates = new HashMap<>() {
        {
            put("Удмуртcкая Республика", new double[]{60.0, 100.0});
            put("Вологодская область", new double[]{60.0391461, 43.1215213});
            put("Республика Дагестан", new double[]{43.0, 47.0});
            put("Хабаровский край", new double[]{51.6312684, 136.121524});
            put("Кемеровская область", new double[]{54.5335781, 87.342861});
            put("Новосибирская область", new double[]{55.8204953, 78.866158});
            put("Мурманская область", new double[]{68.0000418, 33.9999151});
            put("Алтайский край", new double[]{52.6932243, 82.6931424});
            put("Псковская область", new double[]{57.5358729, 28.8586826});
            put("Республика Татарстан", new double[]{55.448217, 50.4763591});
            put("Саратовская область", new double[]{51.6520555, 46.8631952});
            put("Брянская область", new double[]{52.8873315, 33.415853});
            put("Красноярский край", new double[]{63.3233807, 97.0979974});
            put("Смоленская область", new double[]{55.0343496, 33.0192065});
            put("Чувашская Республика", new double[]{55.4259922, 47.0849429});
            put("Магаданская область", new double[]{63.5515028, 154.014726});
            put("Костромская область", new double[]{58.424756, 44.2533273});
            put("Тамбовская область", new double[]{52.9019574, 41.3578918});
            put("Краснодарский край", new double[]{45.7684014, 39.0261044});
            put("Калининградская область", new double[]{54.7293041, 21.1489473});
            put("Тульская область", new double[]{53.9570701, 37.3690909});
            put("Московская область", new double[]{55.5043158, 38.0353929});
            put("Иркутская область", new double[]{56.6370122, 104.719221});
            put("Томская область", new double[]{58.6124279, 82.0475315});
            put("Ивановская область", new double[]{56.9167446, 41.4352137});
            put("Омская область", new double[]{56.0935263, 73.5099936});
            put("Белгородская область", new double[]{50.7080119, 37.5837615});
            put("Амурская область", new double[]{52.8032368, 128.437295});
            put("Тверская область", new double[]{57.1134475, 35.1744428});
            put("Республика Калмыкия", new double[]{46.9017116, 45.249161});
            put("Камчатский край", new double[]{57.1914882, 160.0383819});
            put("Курганская область", new double[]{55.851964, 63.7722525});
            put("Ленинградская область", new double[]{60.1853296, 32.3925325});
            put("Волгоградская область", new double[]{49.6048339, 44.2903582});
            put("Воронежская область", new double[]{50.9800393, 40.1506507});
            put("Забайкальский край", new double[]{52.248521, 115.956325});
            put("Республика Алтай", new double[]{50.7114101, 86.8572186});
            put("Челябинская область", new double[]{54.8560272, 57.1402678});
            put("Липецкая область", new double[]{52.6935178, 39.1122664});
            put("Ненецкий автономный округ", new double[]{67.6783253, 57.0626853});
            put("Еврейская автономная область", new double[]{48.5601613, 132.2775662});
            put("Кабардино-Балкарская Республика", new double[]{43.4428286, 43.4204809});
            put("Ульяновская область", new double[]{54.1463177, 47.2324921});
            put("Республика Марий Эл", new double[]{56.5767504, 47.8817512});
            put("Ямало-Ненецкий автономный округ", new double[]{67.1471631, 74.3415488});
            put("Ханты-Мансийский автономный округ", new double[]{63.0518178, 74.4913089});
            put("Пензенская область", new double[]{53.1655415, 44.7879181});
            put("Ростовская область", new double[]{47.6222451, 40.7957942});
            put("Ярославская область", new double[]{57.7781976, 39.0021095});
            put("Курская область", new double[]{51.6568453, 36.4852695});
            put("Приморский край", new double[]{45.0819456, 134.726645});
            put("Республика Бурятия", new double[]{52.7182426, 109.492143});
            put("Чукотский автономный округ", new double[]{66.0006475, 169.4900869});
            put("Республика Мордовия", new double[]{54.4419829, 44.4661144});
            put("Карачаево-Черкесская Республика", new double[]{43.7368326, 41.7267991});
            put("Республика Саха (Якутия)", new double[]{66.941626, 129.642371});
            put("Республика Адыгея", new double[]{44.6939006, 40.1520421});
            put("Республика Крым", new double[]{45.6856952, 33.9329411});
            put("Тюменская область", new double[]{58.8206488, 70.3658837});
            put("Новгородская область", new double[]{58.2843833, 32.5169757});
            put("Свердловская область", new double[]{58.6414755, 61.8021546});
            put("Орловская область", new double[]{52.8392765, 36.4251709});
            put("Республика Ингушетия", new double[]{43.1655309, 44.979682});
            put("Калужская область", new double[]{54.4382773, 35.5272854});
            put("Республика Северная Осетия - Алания", new double[]{42.9920711, 44.2636348});
            put("Республика Хакасия", new double[]{53.4399379, 90.0664303});
            put("Сахалинская область", new double[]{49.7219665, 143.448533});
            put("Нижегородская область", new double[]{55.4718033, 44.0911594});
            put("Архангельская область", new double[]{63.5589686, 43.1221646});
            put("Пермский край", new double[]{58.5951603, 56.3159546});
            put("Республика Тыва", new double[]{51.4017149, 93.8582593});
            put("Владимирская область", new double[]{56.0503336, 40.6561633});
            put("Самарская область", new double[]{52.676757, 50.5873689});
            put("Чеченская Республика", new double[]{43.4, 45.7});
            put("Оренбургская область", new double[]{52.0269262, 54.7276647});
            put("Рязанская область", new double[]{54.4226732, 40.5705246});
            put("Кировская область", new double[]{58.563072, 50.1824997});
            put("Астраханская область", new double[]{47.1878186, 47.608851});
            put("Ставропольский край", new double[]{44.8632577, 43.4406913});
            put("Республика Коми", new double[]{63.9881421, 54.3326073});
            put("Республика Башкортостан", new double[]{54.4747553, 55.9784582});
            put("Республика Карелия", new double[]{62.6194031, 33.4920267});
        }};
}
