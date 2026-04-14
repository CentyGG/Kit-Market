package com.example.kit_market.data.fake

import com.example.kit_market.domain.model.Category
import com.example.kit_market.domain.model.Product

object FakeProducts {

    val categories = listOf(
        Category(id = 1, name = "Фрукты"),
        Category(id = 2, name = "Молочные продукты"),
        Category(id = 3, name = "Выпечка"),
        Category(id = 4, name = "Напитки")
    )

    val products = listOf(
        // Фрукты
        Product(
            id = 1, name = "Бананы", description = "Спелые бананы из Эквадора. Сладкие и ароматные, идеально подходят для перекуса или приготовления десертов.",
            imageUrl = "https://placehold.co/300x300/FFEB3B/000000?text=Бананы", price = 89.90,
            calories = 89, protein = 1.1, fat = 0.3, carbs = 22.8, categoryId = 1
        ),
        Product(
            id = 2, name = "Яблоки Голден", description = "Сочные яблоки сорта Голден. Отличаются сладким вкусом и хрустящей мякотью.",
            imageUrl = "https://placehold.co/300x300/8BC34A/FFFFFF?text=Яблоки", price = 129.90,
            calories = 52, protein = 0.3, fat = 0.2, carbs = 13.8, categoryId = 1
        ),
        Product(
            id = 3, name = "Апельсины", description = "Свежие апельсины из Марокко. Богаты витамином C, идеальны для свежевыжатого сока.",
            imageUrl = "https://placehold.co/300x300/FF9800/FFFFFF?text=Апельсины", price = 109.90,
            calories = 47, protein = 0.9, fat = 0.1, carbs = 11.7, categoryId = 1
        ),
        Product(
            id = 4, name = "Виноград", description = "Сладкий виноград без косточек. Нежная кожица и сочная мякоть.",
            imageUrl = "https://placehold.co/300x300/9C27B0/FFFFFF?text=Виноград", price = 249.90,
            calories = 69, protein = 0.7, fat = 0.2, carbs = 18.1, categoryId = 1
        ),
        Product(
            id = 5, name = "Манго", description = "Тропическое манго. Ароматное и сладкое, с нежной мякотью.",
            imageUrl = "https://placehold.co/300x300/FF5722/FFFFFF?text=Манго", price = 199.90,
            calories = 60, protein = 0.8, fat = 0.4, carbs = 15.0, categoryId = 1
        ),
        Product(
            id = 6, name = "Груши", description = "Сочные груши сорта Конференция. Мягкая и сладкая мякоть.",
            imageUrl = "https://placehold.co/300x300/CDDC39/000000?text=Груши", price = 149.90,
            calories = 57, protein = 0.4, fat = 0.1, carbs = 15.2, categoryId = 1
        ),
        Product(
            id = 7, name = "Киви", description = "Свежие киви из Новой Зеландии. Кисло-сладкий вкус, богаты витаминами.",
            imageUrl = "https://placehold.co/300x300/4CAF50/FFFFFF?text=Киви", price = 179.90,
            calories = 61, protein = 1.1, fat = 0.5, carbs = 14.7, categoryId = 1
        ),
        Product(
            id = 8, name = "Лимоны", description = "Сочные лимоны. Незаменимы на кухне для напитков и приготовления блюд.",
            imageUrl = "https://placehold.co/300x300/FFC107/000000?text=Лимоны", price = 99.90,
            calories = 29, protein = 1.1, fat = 0.3, carbs = 9.3, categoryId = 1
        ),

        // Молочные продукты
        Product(
            id = 9, name = "Молоко 3.2%", description = "Пастеризованное молоко 3.2% жирности. Свежее и натуральное.",
            imageUrl = "https://placehold.co/300x300/E3F2FD/000000?text=Молоко", price = 89.90,
            calories = 59, protein = 2.9, fat = 3.2, carbs = 4.7, categoryId = 2
        ),
        Product(
            id = 10, name = "Кефир 1%", description = "Кефир с низким содержанием жира. Полезен для пищеварения.",
            imageUrl = "https://placehold.co/300x300/E8EAF6/000000?text=Кефир", price = 69.90,
            calories = 40, protein = 3.0, fat = 1.0, carbs = 4.0, categoryId = 2
        ),
        Product(
            id = 11, name = "Сметана 15%", description = "Натуральная сметана 15% жирности. Нежный сливочный вкус.",
            imageUrl = "https://placehold.co/300x300/FFF3E0/000000?text=Сметана", price = 79.90,
            calories = 162, protein = 2.6, fat = 15.0, carbs = 3.6, categoryId = 2
        ),
        Product(
            id = 12, name = "Творог 5%", description = "Мягкий творог 5% жирности. Источник белка и кальция.",
            imageUrl = "https://placehold.co/300x300/FFFDE7/000000?text=Творог", price = 99.90,
            calories = 121, protein = 17.2, fat = 5.0, carbs = 1.8, categoryId = 2
        ),
        Product(
            id = 13, name = "Йогурт натуральный", description = "Натуральный йогурт без добавок. Подходит для завтрака и перекуса.",
            imageUrl = "https://placehold.co/300x300/F3E5F5/000000?text=Йогурт", price = 59.90,
            calories = 66, protein = 5.0, fat = 3.2, carbs = 3.5, categoryId = 2
        ),
        Product(
            id = 14, name = "Сыр Российский", description = "Полутвёрдый сыр с мягким сливочным вкусом. 50% жирности.",
            imageUrl = "https://placehold.co/300x300/FFE0B2/000000?text=Сыр", price = 349.90,
            calories = 363, protein = 24.1, fat = 29.5, carbs = 0.3, categoryId = 2
        ),
        Product(
            id = 15, name = "Масло сливочное", description = "Натуральное сливочное масло 82.5% жирности. Высшего качества.",
            imageUrl = "https://placehold.co/300x300/FFF9C4/000000?text=Масло", price = 159.90,
            calories = 748, protein = 0.5, fat = 82.5, carbs = 0.8, categoryId = 2
        ),
        Product(
            id = 16, name = "Ряженка 4%", description = "Топлёное молоко с нежным карамельным вкусом. 4% жирности.",
            imageUrl = "https://placehold.co/300x300/FFCCBC/000000?text=Ряженка", price = 74.90,
            calories = 67, protein = 2.8, fat = 4.0, carbs = 4.2, categoryId = 2
        ),

        // Выпечка
        Product(
            id = 17, name = "Хлеб белый", description = "Свежий белый хлеб из пшеничной муки высшего сорта. Мягкий мякиш и хрустящая корочка.",
            imageUrl = "https://placehold.co/300x300/D7CCC8/000000?text=Хлеб", price = 49.90,
            calories = 265, protein = 9.2, fat = 3.2, carbs = 49.1, categoryId = 3
        ),
        Product(
            id = 18, name = "Батон нарезной", description = "Классический нарезной батон. Идеально подходит для бутербродов.",
            imageUrl = "https://placehold.co/300x300/BCAAA4/FFFFFF?text=Батон", price = 39.90,
            calories = 264, protein = 7.5, fat = 2.9, carbs = 51.4, categoryId = 3
        ),
        Product(
            id = 19, name = "Круассаны", description = "Нежные слоёные круассаны. Воздушное тесто с маслянистым вкусом.",
            imageUrl = "https://placehold.co/300x300/FFAB91/000000?text=Круассаны", price = 129.90,
            calories = 406, protein = 8.2, fat = 21.0, carbs = 45.5, categoryId = 3
        ),
        Product(
            id = 20, name = "Булочки с маком", description = "Сдобные булочки с маковой начинкой. Ароматные и мягкие.",
            imageUrl = "https://placehold.co/300x300/CE93D8/FFFFFF?text=Булочки", price = 89.90,
            calories = 316, protein = 7.3, fat = 9.1, carbs = 51.0, categoryId = 3
        ),
        Product(
            id = 21, name = "Хлеб бородинский", description = "Ржаной хлеб с кориандром. Насыщенный вкус и аромат.",
            imageUrl = "https://placehold.co/300x300/795548/FFFFFF?text=Бородинский", price = 59.90,
            calories = 208, protein = 6.8, fat = 1.3, carbs = 39.8, categoryId = 3
        ),
        Product(
            id = 22, name = "Пирожки с яблоком", description = "Домашние пирожки с яблочной начинкой. Из дрожжевого теста.",
            imageUrl = "https://placehold.co/300x300/FF8A65/FFFFFF?text=Пирожки", price = 45.90,
            calories = 289, protein = 5.2, fat = 8.1, carbs = 48.3, categoryId = 3
        ),
        Product(
            id = 23, name = "Лаваш тонкий", description = "Тонкий армянский лаваш. Подходит для шаурмы и закусок.",
            imageUrl = "https://placehold.co/300x300/EFEBE9/000000?text=Лаваш", price = 69.90,
            calories = 236, protein = 7.9, fat = 1.0, carbs = 47.6, categoryId = 3
        ),
        Product(
            id = 24, name = "Багет французский", description = "Хрустящий французский багет. Идеально сочетается с сыром.",
            imageUrl = "https://placehold.co/300x300/A1887F/FFFFFF?text=Багет", price = 79.90,
            calories = 274, protein = 10.0, fat = 1.3, carbs = 56.4, categoryId = 3
        ),

        // Напитки
        Product(
            id = 25, name = "Вода минеральная", description = "Минеральная вода из горных источников. Негазированная, 1.5 л.",
            imageUrl = "https://placehold.co/300x300/E0F7FA/000000?text=Вода", price = 49.90,
            calories = 0, protein = 0.0, fat = 0.0, carbs = 0.0, categoryId = 4
        ),
        Product(
            id = 26, name = "Сок апельсиновый", description = "Натуральный апельсиновый сок прямого отжима. 1 л.",
            imageUrl = "https://placehold.co/300x300/FFE0B2/000000?text=Сок", price = 129.90,
            calories = 45, protein = 0.7, fat = 0.2, carbs = 10.4, categoryId = 4
        ),
        Product(
            id = 27, name = "Чай чёрный", description = "Индийский чёрный чай. Крупнолистовой, 100 пакетиков.",
            imageUrl = "https://placehold.co/300x300/3E2723/FFFFFF?text=Чай", price = 189.90,
            calories = 1, protein = 0.1, fat = 0.0, carbs = 0.3, categoryId = 4
        ),
        Product(
            id = 28, name = "Кофе молотый", description = "Арабика 100%. Средняя обжарка, насыщенный аромат. 250 г.",
            imageUrl = "https://placehold.co/300x300/4E342E/FFFFFF?text=Кофе", price = 299.90,
            calories = 2, protein = 0.1, fat = 0.0, carbs = 0.3, categoryId = 4
        ),
        Product(
            id = 29, name = "Лимонад", description = "Газированный лимонад с натуральным соком лимона. 1 л.",
            imageUrl = "https://placehold.co/300x300/FFF176/000000?text=Лимонад", price = 79.90,
            calories = 42, protein = 0.0, fat = 0.0, carbs = 10.6, categoryId = 4
        ),
        Product(
            id = 30, name = "Морс клюквенный", description = "Натуральный клюквенный морс. Освежающий кисло-сладкий вкус. 1 л.",
            imageUrl = "https://placehold.co/300x300/E57373/FFFFFF?text=Морс", price = 99.90,
            calories = 46, protein = 0.0, fat = 0.0, carbs = 11.0, categoryId = 4
        ),
        Product(
            id = 31, name = "Компот вишнёвый", description = "Домашний вишнёвый компот. Натуральный состав, без консервантов. 1 л.",
            imageUrl = "https://placehold.co/300x300/C62828/FFFFFF?text=Компот", price = 89.90,
            calories = 57, protein = 0.4, fat = 0.0, carbs = 14.2, categoryId = 4
        ),
        Product(
            id = 32, name = "Какао", description = "Растворимый какао-напиток. Насыщенный шоколадный вкус. 200 г.",
            imageUrl = "https://placehold.co/300x300/5D4037/FFFFFF?text=Какао", price = 159.90,
            calories = 374, protein = 24.3, fat = 15.0, carbs = 10.2, categoryId = 4
        )
    )
}
