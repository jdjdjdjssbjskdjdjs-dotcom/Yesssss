package com.example.engine

import kotlin.random.Random

enum class PlayerPosition { FW, MF, DF, GK }

data class Player(
    val nameEn: String,
    val nameFa: String,
    val position: PlayerPosition,
    val rating: Int
)

data class Team(
    val id: Int,
    val nameEn: String,
    val nameFa: String,
    val flag: String,
    val rating: Int,
    val groupName: String, // "A" to "L"
    val players: List<Player>
)

data class TeamFlag(
    val nameEn: String,
    val nameFa: String,
    val flagUrl: String
)

object FlagStorage {
    val TEAM_FLAGS = listOf(
        TeamFlag("USA", "آمریکا", "https://flagcdn.com/w160/us.png"),
        TeamFlag("Ecuador", "اکوادور", "https://flagcdn.com/w160/ec.png"),
        TeamFlag("Cameroon", "کامرون", "https://flagcdn.com/w160/cm.png"),
        TeamFlag("New Zealand", "نیوزیلند", "https://flagcdn.com/w160/nz.png"),
        TeamFlag("England", "انگلستان", "https://flagcdn.com/w160/gb-eng.png"),
        TeamFlag("Iran", "ایران", "https://flagcdn.com/w160/ir.png"),
        TeamFlag("Ukraine", "اوکراین", "https://flagcdn.com/w160/ua.png"),
        TeamFlag("Nigeria", "نیجریه", "https://flagcdn.com/w160/ng.png"),
        TeamFlag("Argentina", "آرژانتین", "https://flagcdn.com/w160/ar.png"),
        TeamFlag("Morocco", "مراکش", "https://flagcdn.com/w160/ma.png"),
        TeamFlag("Poland", "لهستان", "https://flagcdn.com/w160/pl.png"),
        TeamFlag("Australia", "استرالیا", "https://flagcdn.com/w160/au.png"),
        TeamFlag("France", "فرانسه", "https://flagcdn.com/w160/fr.png"),
        TeamFlag("Japan", "ژاپن", "https://flagcdn.com/w160/jp.png"),
        TeamFlag("Austria", "اتریش", "https://flagcdn.com/w160/at.png"),
        TeamFlag("Egypt", "مصر", "https://flagcdn.com/w160/eg.png"),
        TeamFlag("Brazil", "برزیل", "https://flagcdn.com/w160/br.png"),
        TeamFlag("Switzerland", "سوئیس", "https://flagcdn.com/w160/ch.png"),
        TeamFlag("Uzbekistan", "ازبکستان", "https://flagcdn.com/w160/uz.png"),
        TeamFlag("Ghana", "غنا", "https://flagcdn.com/w160/gh.png"),
        TeamFlag("Belgium", "بلژیک", "https://flagcdn.com/w160/be.png"),
        TeamFlag("Croatia", "کرواسی", "https://flagcdn.com/w160/hr.png"),
        TeamFlag("Canada", "کانادا", "https://flagcdn.com/w160/ca.png"),
        TeamFlag("South Korea", "کره جنوبی", "https://flagcdn.com/w160/kr.png"),
        TeamFlag("Spain", "اسپانیا", "https://flagcdn.com/w160/es.png"),
        TeamFlag("Uruguay", "اروگوئه", "https://flagcdn.com/w160/uy.png"),
        TeamFlag("Saudi Arabia", "عربستان", "https://flagcdn.com/w160/sa.png"),
        TeamFlag("Senegal", "سنگال", "https://flagcdn.com/w160/sn.png"),
        TeamFlag("Portugal", "پرتغال", "https://flagcdn.com/w160/pt.png"),
        TeamFlag("Colombia", "کلمبیا", "https://flagcdn.com/w160/co.png"),
        TeamFlag("Turkey", "ترکیه", "https://flagcdn.com/w160/tr.png"),
        TeamFlag("Ivory Coast", "ساحل عاج", "https://flagcdn.com/w160/ci.png"),
        TeamFlag("Italy", "ایتالیا", "https://flagcdn.com/w160/it.png"),
        TeamFlag("Denmark", "دانمارک", "https://flagcdn.com/w160/dk.png"),
        TeamFlag("Iraq", "عراق", "https://flagcdn.com/w160/iq.png"),
        TeamFlag("Algeria", "الجزایر", "https://flagcdn.com/w160/dz.png"),
        TeamFlag("Netherlands", "هلند", "https://flagcdn.com/w160/nl.png"),
        TeamFlag("Mexico", "مکزیک", "https://flagcdn.com/w160/mx.png"),
        TeamFlag("Qatar", "قطر", "https://flagcdn.com/w160/qa.png"),
        TeamFlag("South Africa", "آفریقای جنوبی", "https://flagcdn.com/w160/za.png"),
        TeamFlag("Germany", "آلمان", "https://flagcdn.com/w160/de.png"),
        TeamFlag("Chile", "شیلی", "https://flagcdn.com/w160/cl.png"),
        TeamFlag("Wales", "ولز", "https://flagcdn.com/w160/gb-wls.png"),
        TeamFlag("Tunisia", "تونس", "https://flagcdn.com/w160/tn.png"),
        TeamFlag("Sweden", "سوئد", "https://flagcdn.com/w160/se.png"),
        TeamFlag("Peru", "پرو", "https://flagcdn.com/w160/pe.png"),
        TeamFlag("Jordan", "اردن", "https://flagcdn.com/w160/jo.png"),
        TeamFlag("Mali", "مالی", "https://flagcdn.com/w160/ml.png")
    )

    fun getFlagUrl(nameEn: String): String {
        return TEAM_FLAGS.firstOrNull { it.nameEn.equals(nameEn, ignoreCase = true) }?.flagUrl
            ?: "https://flagcdn.com/w160/un.png"
    }
}

data class MatchEvent(
    val minute: Int,
    val type: EventType,
    val teamId: Int, // 0 for Home, 1 for Away
    val teamFa: String,
    val playerFa: String,
    val playerEn: String,
    val assistFa: String? = null,
    val assistEn: String? = null,
    val commentaryFa: String
)

enum class EventType {
    GOAL, YELLOW_CARD, RED_CARD, KICKOFF, HALF_TIME, FULL_TIME, NEAR_MISS
}

object SimulationEngine {

    val TEAMS = listOf(
        // GROUP A
        Team(1, "USA", "آمریکا", "🇺🇸", 82, "A", listOf(
            Player("Christian Pulisic", "کریستین پولیشیچ", PlayerPosition.FW, 85),
            Player("Folarin Balogun", "فولارین بالوگان", PlayerPosition.FW, 80),
            Player("Weston McKennie", "وستون مک‌کنی", PlayerPosition.MF, 81),
            Player("Tyler Adams", "تایلر آدامز", PlayerPosition.MF, 79),
            Player("Antonee Robinson", "آنتونی رابینسون", PlayerPosition.DF, 80),
            Player("Matt Turner", "مت ترنر", PlayerPosition.GK, 77)
        )),
        Team(2, "Ecuador", "اکوادور", "🇪🇨", 80, "A", listOf(
            Player("Enner Valencia", "انر والنسیا", PlayerPosition.FW, 79),
            Player("Kendry Paez", "کندری پائز", PlayerPosition.MF, 78),
            Player("Moises Caicedo", "مویزس کایسدو", PlayerPosition.MF, 84),
            Player("Piero Hincapie", "پیرو هینکاپیه", PlayerPosition.DF, 82),
            Player("Pervis Estupinan", "پرویس استوپینیان", PlayerPosition.DF, 80),
            Player("Alexander Dominguez", "الکساندر دومینگز", PlayerPosition.GK, 75)
        )),
        Team(3, "Cameroon", "کامرون", "🇨🇲", 78, "A", listOf(
            Player("Vincent Aboubakar", "وینسنت ابوبکر", PlayerPosition.FW, 78),
            Player("Bryan Mbeumo", "برایان امبومو", PlayerPosition.FW, 81),
            Player("Andre-Frank Zambo Anguissa", "فرانک آنگیسا", PlayerPosition.MF, 80),
            Player("Olivier Kemen", "اولیویر کمن", PlayerPosition.MF, 74),
            Player("Christopher Wooh", "کریستوفر ووه", PlayerPosition.DF, 76),
            Player("Andre Onana", "آندره اونانا", PlayerPosition.GK, 84)
        )),
        Team(4, "New Zealand", "نیوزیلند", "🇳🇿", 70, "A", listOf(
            Player("Chris Wood", "کریس وود", PlayerPosition.FW, 77),
            Player("Ben Waine", "بن وین", PlayerPosition.FW, 68),
            Player("Sarpreet Singh", "سارپریت سینگ", PlayerPosition.MF, 71),
            Player("Matthew Garbett", "متیو گاربت", PlayerPosition.MF, 69),
            Player("Libato Cacace", "لیبراتو کاکاچه", PlayerPosition.DF, 72),
            Player("Oliver Sail", "الیور سیل", PlayerPosition.GK, 67)
        )),

        // GROUP B
        Team(5, "England", "انگلستان", "🏴󠁧󠁢󠁥󠁮󠁧󠁿", 89, "B", listOf(
            Player("Harry Kane", "هری کین", PlayerPosition.FW, 90),
            Player("Jude Bellingham", "جود بلینگهام", PlayerPosition.MF, 90),
            Player("Bukayo Saka", "بوکایو ساکا", PlayerPosition.FW, 87),
            Player("Declan Rice", "دکلان رایس", PlayerPosition.MF, 87),
            Player("John Stones", "جان استونز", PlayerPosition.DF, 85),
            Player("Jordan Pickford", "جوردن پیکفورد", PlayerPosition.GK, 83)
        )),
        Team(6, "Iran", "ایران", "🇮🇷", 81, "B", listOf(
            Player("Mehdi Taremi", "مهدی طارمی", PlayerPosition.FW, 83),
            Player("Sardar Azmoun", "سردار آزمون", PlayerPosition.FW, 81),
            Player("Alireza Jahanbakhsh", "علیرضا جهانبخش", PlayerPosition.MF, 75),
            Player("Saman Ghoddos", "سامان قدوس", PlayerPosition.MF, 76),
            Player("Milad Mohammadi", "میلاد محمدی", PlayerPosition.DF, 74),
            Player("Alireza Beiranvand", "علیرضا بیرانوند", PlayerPosition.GK, 76)
        )),
        Team(7, "Ukraine", "اوکراین", "🇺🇦", 79, "B", listOf(
            Player("Artem Dovbyk", "آرتم دووبیک", PlayerPosition.FW, 82),
            Player("Mykhailo Mudryk", "میخایلو مودریک", PlayerPosition.FW, 78),
            Player("Georgiy Sudakov", "گئورگی سوداکوف", PlayerPosition.MF, 79),
            Player("Oleksandr Zinchenko", "الکساندر زینچنکو", PlayerPosition.DF, 79),
            Player("Illia Zabarnyi", "ایلیا زابارنی", PlayerPosition.DF, 80),
            Player("Andriy Lunin", "آندری لونین", PlayerPosition.GK, 81)
        )),
        Team(8, "Nigeria", "نیجریه", "🇳🇬", 80, "B", listOf(
            Player("Victor Osimhen", "ویکتور اوسیمهن", PlayerPosition.FW, 87),
            Player("Ademola Lookman", "آدمولا لوکمن", PlayerPosition.FW, 82),
            Player("Alex Iwobi", "الکس ایووبی", PlayerPosition.MF, 76),
            Player("Wilfred Ndidi", "ویلفرد ندیدی", PlayerPosition.MF, 79),
            Player("William Troost-Ekong", "ویلیام تروست اکونگ", PlayerPosition.DF, 77),
            Player("Stanley Nwabali", "استنلی نوابالی", PlayerPosition.GK, 74)
        )),

        // GROUP C
        Team(9, "Argentina", "آرژانتین", "🇦🇷", 92, "C", listOf(
            Player("Lionel Messi", "لیونل مسی", PlayerPosition.FW, 90),
            Player("Lautaro Martinez", "لوتارو مارتینز", PlayerPosition.FW, 88),
            Player("Alexis Mac Allister", "الکسیس مک‌آلیستر", PlayerPosition.MF, 86),
            Player("Rodrigo De Paul", "رودریگو دپل", PlayerPosition.MF, 84),
            Player("Cristian Romero", "کریستین رومرو", PlayerPosition.DF, 87),
            Player("Emiliano Martinez", "امیلیانو مارتینز", PlayerPosition.GK, 88)
        )),
        Team(10, "Morocco", "مراکش", "🇲🇦", 85, "C", listOf(
            Player("Youssef En-Nesyri", "یوسف النصیری", PlayerPosition.FW, 81),
            Player("Hakim Ziyech", "حکیم زیاش", PlayerPosition.MF, 80),
            Player("Sofyan Amrabat", "سفیان امرابط", PlayerPosition.MF, 80),
            Player("Achraf Hakimi", "اشرف حکیمی", PlayerPosition.DF, 84),
            Player("Nayef Aguerd", "نایف اکرد", PlayerPosition.DF, 80),
            Player("Yassine Bounou", "یاسین بونو", PlayerPosition.GK, 83)
        )),
        Team(11, "Poland", "لهستان", "🇵🇱", 79, "C", listOf(
            Player("Robert Lewandowski", "روبرت لواندوفسکی", PlayerPosition.FW, 86),
            Player("Karol Swiderski", "کارول سویدرسکی", PlayerPosition.FW, 75),
            Player("Piotr Zielinski", "پیوتر زیلینسکی", PlayerPosition.MF, 81),
            Player("Sebastian Szymanski", "سباستین شیمانسکی", PlayerPosition.MF, 77),
            Player("Jakub Kiwior", "یاکوب کیویور", PlayerPosition.DF, 77),
            Player("Wojciech Szczesny", "وویچک شزنی", PlayerPosition.GK, 82)
        )),
        Team(12, "Australia", "استرالیا", "🇦🇺", 77, "C", listOf(
            Player("Mitch Duke", "میچل دوک", PlayerPosition.FW, 72),
            Player("Craig Goodwin", "کریگ گودوین", PlayerPosition.FW, 75),
            Player("Jackson Irvine", "جکسون اروین", PlayerPosition.MF, 74),
            Player("Connor Metcalfe", "کانر متکالف", PlayerPosition.MF, 71),
            Player("Harry Souttar", "هری سوتار", PlayerPosition.DF, 74),
            Player("Mathew Ryan", "متیو رایان", PlayerPosition.GK, 75)
        )),

        // GROUP D
        Team(13, "France", "فرانسه", "🇫🇷", 91, "D", listOf(
            Player("Kylian Mbappe", "کیلیان امباپه", PlayerPosition.FW, 91),
            Player("Antoine Griezmann", "آنتوان گریزمان", PlayerPosition.MF, 86),
            Player("Ousmane Dembele", "عثمان دمبله", PlayerPosition.FW, 85),
            Player("Aurelien Tchouameni", "اورلین شوامنی", PlayerPosition.MF, 86),
            Player("William Saliba", "ویلیام سالیبا", PlayerPosition.DF, 88),
            Player("Mike Maignan", "مایک مانیان", PlayerPosition.GK, 86)
        )),
        Team(14, "Japan", "ژاپن", "🇯🇵", 82, "D", listOf(
            Player("Ayase Ueda", "آیاسه اوئدا", PlayerPosition.FW, 77),
            Player("Kaoru Mitoma", "کائورو میتوما", PlayerPosition.FW, 82),
            Player("Takefusa Kubo", "تاکفوسا کوبو", PlayerPosition.MF, 82),
            Player("Wataru Endo", "واتارو اندو", PlayerPosition.MF, 80),
            Player("Takehiro Tomiyasu", "تاکهیرو تومیاسو", PlayerPosition.DF, 80),
            Player("Zion Suzuki", "زیون سوزوکی", PlayerPosition.GK, 74)
        )),
        Team(15, "Austria", "اتریش", "🇦🇹", 80, "D", listOf(
            Player("Michael Gregoritsch", "میشائیل گرگوریچ", PlayerPosition.FW, 77),
            Player("Marcel Sabitzer", "مارسل سابیتزر", PlayerPosition.MF, 81),
            Player("Konrad Laimer", "کنراد لایمر", PlayerPosition.MF, 80),
            Player("Christoph Baumgartner", "کریستوف باومگارتنر", PlayerPosition.MF, 79),
            Player("Kevin Danso", "کوین دانسو", PlayerPosition.DF, 79),
            Player("Patrick Pentz", "پاتریک پنتز", PlayerPosition.GK, 75)
        )),
        Team(16, "Egypt", "مصر", "🇪🇬", 79, "D", listOf(
            Player("Mohamed Salah", "محمد صلاح", PlayerPosition.FW, 88),
            Player("Mostafa Mohamed", "مصطفی محمد", PlayerPosition.FW, 78),
            Player("Trezeguet", "ترزگه", PlayerPosition.MF, 76),
            Player("Mohamed Elneny", "محمد الننی", PlayerPosition.MF, 74),
            Player("Ahmed Hegazi", "احمد حجازی", PlayerPosition.DF, 75),
            Player("Mohamed El Shenawy", "محمد الشناوی", PlayerPosition.GK, 76)
        )),

        // GROUP E
        Team(17, "Brazil", "برزیل", "🇧🇷", 90, "E", listOf(
            Player("Vinicius Junior", "وینیسیوس جونیور", PlayerPosition.FW, 90),
            Player("Rodrygo", "رودریگو", PlayerPosition.FW, 86),
            Player("Neymar Jr", "نیمار جونیور", PlayerPosition.MF, 85),
            Player("Bruno Guimaraes", "برونو گیمارش", PlayerPosition.MF, 86),
            Player("Marquinhos", "مارکینیوش", PlayerPosition.DF, 86),
            Player("Alisson Becker", "آلیسون بکر", PlayerPosition.GK, 87)
        )),
        Team(18, "Switzerland", "سوئیس", "🇨🇭", 81, "E", listOf(
            Player("Breel Embolo", "بریل امبولو", PlayerPosition.FW, 78),
            Player("Granit Xhaka", "گرانیت ژاکا", PlayerPosition.MF, 83),
            Player("Remo Freuler", "رمو فرولر", PlayerPosition.MF, 78),
            Player("Manuel Akanji", "مانوئل آکانجی", PlayerPosition.DF, 83),
            Player("Ricardo Rodriguez", "ریکاردو رودریگز", PlayerPosition.DF, 76),
            Player("Yann Sommer", "یان زومر", PlayerPosition.GK, 82)
        )),
        Team(19, "Uzbekistan", "ازبکستان", "🇺🇿", 74, "E", listOf(
            Player("Eldor Shomurodov", "الدور شومورودوف", PlayerPosition.FW, 75),
            Player("Abbosbek Fayzullaev", "عباس‌بک فیض‌الله‌اف", PlayerPosition.MF, 74),
            Player("Otabek Shukurov", "اتابک شوکوروف", PlayerPosition.MF, 71),
            Player("Jaloliddin Masharipov", "جلال‌الدین ماشاریپوف", PlayerPosition.MF, 72),
            Player("Rustam Ashurmatov", "رستم عاشورماتوف", PlayerPosition.DF, 69),
            Player("Utkir Yusupov", "اوتکیر یوسوپوف", PlayerPosition.GK, 68)
        )),
        Team(20, "Ghana", "غنا", "🇬🇭", 77, "E", listOf(
            Player("Inaki Williams", "ایناکی ویلیامز", PlayerPosition.FW, 79),
            Player("Mohammed Kudus", "محمد قدوس", PlayerPosition.MF, 83),
            Player("Thomas Partey", "توماس پارتی", PlayerPosition.MF, 80),
            Player("Jordan Ayew", "جوردن آیو", PlayerPosition.FW, 75),
            Player("Alexander Djiku", "الکساندر جیکو", PlayerPosition.DF, 76),
            Player("Lawrence Ati-Zigi", "لارنس آتی زیگی", PlayerPosition.GK, 73)
        )),

        // GROUP F
        Team(21, "Belgium", "بلژیک", "🇧🇪", 85, "F", listOf(
            Player("Romelu Lukaku", "روملو لوکاکو", PlayerPosition.FW, 82),
            Player("Kevin De Bruyne", "کوین دی بروینه", PlayerPosition.MF, 89),
            Player("Jeremy Doku", "جرمی دوکو", PlayerPosition.FW, 83),
            Player("Amadou Onana", "آمادو اونانا", PlayerPosition.MF, 80),
            Player("Wout Faes", "ووت فائس", PlayerPosition.DF, 78),
            Player("Koen Casteels", "کوئن کاستیلز", PlayerPosition.GK, 80)
        )),
        Team(22, "Croatia", "کرواسی", "🇭🇷", 84, "F", listOf(
            Player("Andrej Kramaric", "آندری کراماریچ", PlayerPosition.FW, 79),
            Player("Luka Modric", "لوکا مودریچ", PlayerPosition.MF, 85),
            Player("Mateo Kovacic", "متئو کواچیچ", PlayerPosition.MF, 81),
            Player("Mario Pasalic", "ماریو پاشالیچ", PlayerPosition.MF, 79),
            Player("Josko Gvardiol", "یوشکو گواردیول", PlayerPosition.DF, 86),
            Player("Dominik Livakovic", "دومینیک لیواکوویچ", PlayerPosition.GK, 81)
        )),
        Team(23, "Canada", "کانادا", "🇨🇦", 78, "F", listOf(
            Player("Jonathan David", "جاناتان دیوید", PlayerPosition.FW, 81),
            Player("Cyle Larin", "سایل لارین", PlayerPosition.FW, 76),
            Player("Alphonso Davies", "آلفونسو دیویس", PlayerPosition.DF, 82),
            Player("Stephen Eustaquio", "استفن اوستاکیو", PlayerPosition.MF, 78),
            Player("Alistair Johnston", "آلیستر جانستون", PlayerPosition.DF, 76),
            Player("Maxime Crepeau", "ماکسیم کرپو", PlayerPosition.GK, 74)
        )),
        Team(24, "South Korea", "کره جنوبی", "🇰🇷", 80, "F", listOf(
            Player("Heung-min Son", "سون هیونگ مین", PlayerPosition.FW, 86),
            Player("Hee-chan Hwang", "هوانگ هی چان", PlayerPosition.FW, 79),
            Player("Kang-in Lee", "لی کانگ این", PlayerPosition.MF, 80),
            Player("In-beom Hwang", "هوانگ این بوم", PlayerPosition.MF, 77),
            Player("Min-jae Kim", "کیم مین جائه", PlayerPosition.DF, 84),
            Player("Hyeon-woo Jo", "جو هیون وو", PlayerPosition.GK, 75)
        )),

        // GROUP G
        Team(25, "Spain", "اسپانیا", "🇪🇸", 91, "G", listOf(
            Player("Lamine Yamal", "لامین یامال", PlayerPosition.FW, 88),
            Player("Nico Williams", "نیکو ویلیامز", PlayerPosition.FW, 85),
            Player("Rodri", "رودری", PlayerPosition.MF, 91),
            Player("Pedri", "پدری", PlayerPosition.MF, 86),
            Player("Dani Carvajal", "دنی کارواخال", PlayerPosition.DF, 85),
            Player("Unai Simon", "اونای سیمون", PlayerPosition.GK, 85)
        )),
        Team(26, "Uruguay", "اروپوئه", "🇺🇾", 84, "G", listOf(
            Player("Darwin Nunez", "داروین نونیز", PlayerPosition.FW, 82),
            Player("Federico Valverde", "فدریکو والورده", PlayerPosition.MF, 87),
            Player("Rodrigo Bentancur", "رودریگو بنتانکور", PlayerPosition.MF, 81),
            Player("Ronald Araujo", "رونالد آرائوخو", PlayerPosition.DF, 84),
            Player("Mathias Olivera", "ماتیاس اولیورا", PlayerPosition.DF, 78),
            Player("Sergio Rochet", "سرخیو روشت", PlayerPosition.GK, 77)
        )),
        Team(27, "Saudi Arabia", "عربستان", "🇸🇦", 76, "G", listOf(
            Player("Saleh Al-Shehri", "صالح الشهری", PlayerPosition.FW, 72),
            Player("Salem Al-Dawsari", "سالم الدوسری", PlayerPosition.MF, 76),
            Player("Firas Al-Buraikan", "فراس البریکان", PlayerPosition.FW, 73),
            Player("Mohamed Kanno", "محمد کنو", PlayerPosition.MF, 73),
            Player("Ali Al-Bulayhi", "علی البلیهی", PlayerPosition.DF, 72),
            Player("Mohammed Al-Owais", "محمد العویس", PlayerPosition.GK, 73)
        )),
        Team(28, "Senegal", "سنگال", "🇸🇳", 81, "G", listOf(
            Player("Sadio Mane", "سادیو مانه", PlayerPosition.FW, 82),
            Player("Nicolas Jackson", "نیکولاس جکسون", PlayerPosition.FW, 81),
            Player("Pape Matar Sarr", "پاپ سار", PlayerPosition.MF, 79),
            Player("Idrissa Gueye", "ادریسا گی", PlayerPosition.MF, 76),
            Player("Kalidou Koulibaly", "کالیدو کولیبالی", PlayerPosition.DF, 80),
            Player("Edouard Mendy", "ادوارد مندی", PlayerPosition.GK, 79)
        )),

        // GROUP H
        Team(29, "Portugal", "پرتغال", "🇵🇹", 89, "H", listOf(
            Player("Cristiano Ronaldo", "کریستیانو رونالدو", PlayerPosition.FW, 86),
            Player("Bruno Fernandes", "برونو فرناندز", PlayerPosition.MF, 88),
            Player("Bernardo Silva", "برناردو سیلوا", PlayerPosition.MF, 87),
            Player("Rafael Leao", "رافائل لیائو", PlayerPosition.FW, 86),
            Player("Ruben Dias", "روبن دیاز", PlayerPosition.DF, 88),
            Player("Diogo Costa", "دیوگو کاستا", PlayerPosition.GK, 84)
        )),
        Team(30, "Colombia", "کلمبیا", "🇨🇴", 83, "H", listOf(
            Player("Luis Diaz", "لوئیز دیاز", PlayerPosition.FW, 84),
            Player("James Rodriguez", "خامس رودریگز", PlayerPosition.MF, 81),
            Player("Jhon Duran", "جان دوران", PlayerPosition.FW, 79),
            Player("Richard Rios", "ریچارد ریوس", PlayerPosition.MF, 77),
            Player("Davinson Sanchez", "داوینسون سانچز", PlayerPosition.DF, 78),
            Player("Camilo Vargas", "کامیلو وارگاس", PlayerPosition.GK, 78)
        )),
        Team(31, "Turkey", "ترکیه", "🇹🇷", 81, "H", listOf(
            Player("Baris Alper Yilmaz", "بارش آلپر یلماز", PlayerPosition.FW, 78),
            Player("Arda Guler", "آردا گولر", PlayerPosition.MF, 80),
            Player("Hakan Calhanoglu", "هاکان چالهان‌اوغلو", PlayerPosition.MF, 84),
            Player("Kenan Yildiz", "کنان ییلدیز", PlayerPosition.FW, 77),
            Player("Abdulkerim Bardakci", "عبدالکریم بارداکچی", PlayerPosition.DF, 78),
            Player("Mert Gunok", "مرت گونوک", PlayerPosition.GK, 78)
        )),
        Team(32, "Ivory Coast", "ساحل عاج", "🇨🇮", 78, "H", listOf(
            Player("Sebastien Haller", "سباستین هالر", PlayerPosition.FW, 78),
            Player("Simon Adingra", "سیمون آدینگرا", PlayerPosition.FW, 78),
            Player("Franck Kessie", "فرانک کسیه", PlayerPosition.MF, 81),
            Player("Seko Fofana", "سکو فوفانا", PlayerPosition.MF, 80),
            Player("Evan Ndicka", "ایوان اندیکا", PlayerPosition.DF, 80),
            Player("Yahia Fofana", "یحیی فوفانا", PlayerPosition.GK, 75)
        )),

        // GROUP I
        Team(33, "Italy", "ایتالیا", "🇮🇹", 87, "I", listOf(
            Player("Mateo Retegui", "ماتئو رتگی", PlayerPosition.FW, 80),
            Player("Federico Chiesa", "فدریکو کیزا", PlayerPosition.FW, 81),
            Player("Nicolo Barella", "نیکولو بارلا", PlayerPosition.MF, 87),
            Player("Davide Frattesi", "داویده فراتسی", PlayerPosition.MF, 81),
            Player("Alessandro Bastoni", "الساندرو باستونی", PlayerPosition.DF, 86),
            Player("Gianluigi Donnarumma", "جیانلوئیجی دوناروما", PlayerPosition.GK, 86)
        )),
        Team(34, "Denmark", "دانمارک", "🇩🇰", 81, "I", listOf(
            Player("Rasmus Hojlund", "راسموس هویلوند", PlayerPosition.FW, 79),
            Player("Christian Eriksen", "کریستین اریکسن", PlayerPosition.MF, 79),
            Player("Pierre-Emile Hojbjerg", "پیر امیل هویبرگ", PlayerPosition.MF, 80),
            Player("Jonas Wind", "یوناس ویند", PlayerPosition.FW, 78),
            Player("Andreas Christensen", "آندره‌آ کریستنسن", PlayerPosition.DF, 81),
            Player("Kasper Schmeichel", "کاسپر اشمایکل", PlayerPosition.GK, 77)
        )),
        Team(35, "Iraq", "عراق", "🇮🇶", 75, "I", listOf(
            Player("Aymen Hussein", "ایمن حسین", PlayerPosition.FW, 76),
            Player("Ali Jasim", "علی جاسم", PlayerPosition.MF, 73),
            Player("Ibrahim Bayesh", "ابراهیم بایش", PlayerPosition.MF, 70),
            Player("Amir Al-Ammari", "امیر العماری", PlayerPosition.MF, 69),
            Player("Rebin Sulaka", "ریبین سولاقا", PlayerPosition.DF, 68),
            Player("Jalal Hassan", "جلال حسن", PlayerPosition.GK, 70)
        )),
        Team(36, "Algeria", "الجزایر", "🇩🇿", 79, "I", listOf(
            Player("Amine Gouiri", "امین گوئیری", PlayerPosition.FW, 79),
            Player("Said Benrahma", "سعید بن‌رحمه", PlayerPosition.FW, 77),
            Player("Ismael Bennacer", "اسماعیل بن‌ناصر", PlayerPosition.MF, 81),
            Player("Riyad Mahrez", "ریاض محرز", PlayerPosition.MF, 80),
            Player("Rayan Ait-Nouri", "رایان آیت نوری", PlayerPosition.DF, 80),
            Player("Anthony Mandrea", "آنتونی ماندریا", PlayerPosition.GK, 73)
        )),

        // GROUP J
        Team(37, "Netherlands", "هلند", "🇳🇱", 86, "J", listOf(
            Player("Cody Gakpo", "کودی گاکپو", PlayerPosition.FW, 84),
            Player("Memphis Depay", "ممفیس دپای", PlayerPosition.FW, 80),
            Player("Xavi Simons", "ژاوی سیمونز", PlayerPosition.MF, 84),
            Player("Tijjani Reijnders", "تیجانی رایندرز", PlayerPosition.MF, 81),
            Player("Virgil van Dijk", "ویرجیل فن دایک", PlayerPosition.DF, 88),
            Player("Bart Verbruggen", "بارت فربروخن", PlayerPosition.GK, 81)
        )),
        Team(38, "Mexico", "مکزیک", "🇲🇽", 81, "J", listOf(
            Player("Santiago Gimenez", "سانتیاگو خیمنز", PlayerPosition.FW, 80),
            Player("Hirving Lozano", "هیروینگ لوزانو", PlayerPosition.FW, 78),
            Player("Edson Alvarez", "ادسون آلوارز", PlayerPosition.MF, 81),
            Player("Luis Chavez", "لوئیس چاوز", PlayerPosition.MF, 77),
            Player("Cesar Montes", "سزار مونتس", PlayerPosition.DF, 76),
            Player("Luis Malagon", "لوئیس مالاگون", PlayerPosition.GK, 76)
        )),
        Team(39, "Qatar", "قطر", "🇶🇦", 74, "J", listOf(
            Player("Akram Afif", "اکرم عفیف", PlayerPosition.FW, 78),
            Player("Almoez Ali", "المعز علی", PlayerPosition.FW, 73),
            Player("Hassan Al-Haydos", "حسن الهیدوس", PlayerPosition.MF, 71),
            Player("Karim Boudiaf", "کریم بوضیاف", PlayerPosition.MF, 68),
            Player("Lucas Mendes", "لوکاس مندز", PlayerPosition.DF, 70),
            Player("Meshaal Barsham", "مشعل برشم", PlayerPosition.GK, 71)
        )),
        Team(40, "South Africa", "آفریقای جنوبی", "🇿🇦", 75, "J", listOf(
            Player("Percy Tau", "پرسی تاو", PlayerPosition.FW, 73),
            Player("Themba Zwane", "تمبا زوانه", PlayerPosition.MF, 72),
            Player("Teboho Mokoena", "تبوهو موکوئنا", PlayerPosition.MF, 74),
            Player("Sphephelo Sithole", "اسپفلو سیتوله", PlayerPosition.MF, 69),
            Player("Mothobi Mvala", "موتوبی موالا", PlayerPosition.DF, 70),
            Player("Ronwen Williams", "رونون ویلیامز", PlayerPosition.GK, 76)
        )),

        // GROUP K
        Team(41, "Germany", "آلمان", "🇩🇪", 89, "K", listOf(
            Player("Jamal Musiala", "جمال موسیالا", PlayerPosition.MF, 89),
            Player("Florian Wirtz", "فلوریان ویرتز", PlayerPosition.MF, 89),
            Player("Kai Havertz", "کای هاورتز", PlayerPosition.FW, 84),
            Player("Leroy Sane", "لروی سانه", PlayerPosition.FW, 83),
            Player("Antonio Rudiger", "آنتونیو رودیگر", PlayerPosition.DF, 87),
            Player("Marc-Andre ter Stegen", "مارک آندره ترشتگن", PlayerPosition.GK, 86)
        )),
        Team(42, "Chile", "شیلی", "🇨🇱", 78, "K", listOf(
            Player("Eduardo Vargas", "ادواردو وارگاس", PlayerPosition.FW, 73),
            Player("Alexis Sanchez", "الکسیس سانچز", PlayerPosition.FW, 75),
            Player("Marcelino Nunez", "مارسلینو نونز", PlayerPosition.MF, 75),
            Player("Erick Pulgar", "اریک پولگار", PlayerPosition.MF, 74),
            Player("Guillermo Maripan", "گیلرمو ماریپان", PlayerPosition.DF, 75),
            Player("Brayan Cortes", "برایان کورتس", PlayerPosition.GK, 72)
        )),
        Team(43, "Wales", "ولز", "🏴󠁧󠁢󠁷󠁬󠁳󠁿", 76, "K", listOf(
            Player("Brennan Johnson", "برنان جانسون", PlayerPosition.FW, 79),
            Player("Daniel James", "دانیل جیمز", PlayerPosition.FW, 73),
            Player("Harry Wilson", "هری ویلسون", PlayerPosition.MF, 76),
            Player("Ethan Ampadu", "اتان آمپادو", PlayerPosition.MF, 75),
            Player("Ben Davies", "بن دیویس", PlayerPosition.DF, 74),
            Player("Danny Ward", "دنی وارد", PlayerPosition.GK, 71)
        )),
        Team(44, "Tunisia", "تونس", "🇹🇳", 75, "K", listOf(
            Player("Youssef Msakni", "یوسف مساکنی", PlayerPosition.FW, 72),
            Player("Elias Achouri", "الیاس عاشوری", PlayerPosition.FW, 74),
            Player("Aissa Laidouni", "عیسی لایدونی", PlayerPosition.MF, 75),
            Player("Ellyes Skhiri", "الیاس صخیری", PlayerPosition.MF, 77),
            Player("Montassar Talbi", "منتصر طالبی", PlayerPosition.DF, 74),
            Player("Bechir Ben Said", "بشیر بن سعید", PlayerPosition.GK, 70)
        )),

        // GROUP L
        Team(45, "Sweden", "سوئد", "🇸🇪", 81, "L", listOf(
            Player("Viktor Gyokeres", "ویکتور گیوکرش", PlayerPosition.FW, 86),
            Player("Alexander Isak", "الکساندر ایساک", PlayerPosition.FW, 84),
            Player("Dejan Kulusevski", "دژان کولوسوسکی", PlayerPosition.MF, 82),
            Player("Hugo Larsson", "هوگو لارسون", PlayerPosition.MF, 77),
            Player("Victor Lindelof", "ویکتور لیندلوف", PlayerPosition.DF, 77),
            Player("Robin Olsen", "روبین اولسن", PlayerPosition.GK, 74)
        )),
        Team(46, "Peru", "پرو", "🇵🇪", 77, "L", listOf(
            Player("Gianluca Lapadula", "جیانلوکا لاپادولا", PlayerPosition.FW, 73),
            Player("Edison Flores", "ادیسون فلورس", PlayerPosition.MF, 72),
            Player("Piero Quispe", "پیرو کوئیسپه", PlayerPosition.MF, 71),
            Player("Renato Tapia", "رناتو تاپیا", PlayerPosition.MF, 75),
            Player("Luis Advincula", "لوئیس ادوینکولا", PlayerPosition.DF, 74),
            Player("Pedro Gallese", "پدرو گایسه", PlayerPosition.GK, 75)
        )),
        Team(47, "Jordan", "اردن", "🇯🇴", 73, "L", listOf(
            Player("Yazan Al-Naimat", "یزن النعیمات", PlayerPosition.FW, 74),
            Player("Musa Al-Taamari", "موسی التعمری", PlayerPosition.FW, 76),
            Player("Ali Olwan", "علی علوان", PlayerPosition.FW, 70),
            Player("Nizar Al-Rashdan", "نزار الرشدان", PlayerPosition.MF, 67),
            Player("Yazan Al-Arab", "یزن العرب", PlayerPosition.DF, 69),
            Player("Yazeed Abulaila", "یزید ابولیلی", PlayerPosition.GK, 69)
        )),
        Team(48, "Mali", "مالی", "🇲🇱", 76, "L", listOf(
            Player("El Bilal Toure", "البلال توره", PlayerPosition.FW, 74),
            Player("Lassine Sinayoko", "لاسین سینایوکو", PlayerPosition.FW, 73),
            Player("Yves Bissouma", "ایو بیسوما", PlayerPosition.MF, 79),
            Player("Amadou Haidara", "آمادو هایدارا", PlayerPosition.MF, 77),
            Player("Hamari Traore", "هماری ترائوره", PlayerPosition.DF, 76),
            Player("Djigui Diarra", "جیگی دیارا", PlayerPosition.GK, 70)
        ))
    )

    fun simulateMatch(home: Team, away: Team, isKnockout: Boolean = false): MatchSimulationResult {
        val homeRating = home.rating
        val awayRating = away.rating

        // Calculate base expected goals (weighted by rating and some randomness)
        val ratingDiff = homeRating - awayRating
        
        // Base goals expected around 1.3 for even teams
        val homeBase = 1.3 + (ratingDiff * 0.05)
        val awayBase = 1.3 - (ratingDiff * 0.05)

        // Generate actual goals using simplified Poisson approximation
        var homeGoals = generateGoals(homeBase)
        var awayGoals = generateGoals(awayBase)

        // Ensure no draws in knockout matches
        var extraTimeOccurred = false
        var penaltyShootoutOccurred = false
        var homePens = 0
        var awayPens = 0

        if (isKnockout && homeGoals == awayGoals) {
            extraTimeOccurred = true
            // 35% chance a goal gets scored in extra time for home or away
            val etHomeGoals = if (Random.nextFloat() < 0.25f) 1 else 0
            val etAwayGoals = if (Random.nextFloat() < 0.25f) 1 else 0
            homeGoals += etHomeGoals
            awayGoals += etAwayGoals

            if (homeGoals == awayGoals) {
                penaltyShootoutOccurred = true
                // Sim penalty shootout
                while (homePens == awayPens) {
                    homePens = Random.nextInt(3, 6)
                    awayPens = Random.nextInt(3, 6)
                    if (Random.nextFloat() < 0.1f) { // sudden death or other variations
                        homePens = Random.nextInt(5, 10)
                        awayPens = Random.nextInt(5, 10)
                    }
                }
            }
        }

        // Generate events
        val events = mutableListOf<MatchEvent>()
        
        // Kickoff event
        events.add(MatchEvent(
            minute = 0,
            type = EventType.KICKOFF,
            teamId = 0,
            teamFa = home.nameFa,
            playerFa = "",
            playerEn = "",
            commentaryFa = "بازی با سوت داور در ورزشگاه آغاز می‌شود! ${home.flag} ${home.nameFa} در برابر ${away.flag} ${away.nameFa}."
        ))

        val totalMinutes = if (extraTimeOccurred) 120 else 90

        // Allocate goals to minutes and players
        val homeGoalMinutes = generateUniqueMinutes(homeGoals, totalMinutes)
        val awayGoalMinutes = generateUniqueMinutes(awayGoals, totalMinutes)

        val yellowCards = mutableListOf<MatchEvent>()
        val redCards = mutableListOf<MatchEvent>()

        // Simulate random yellow/red cards
        val totalYellows = Random.nextInt(1, 6)
        val yellowMinutes = generateUniqueMinutes(totalYellows, totalMinutes)
        
        // Red card chance (extremely low)
        val hasRedCard = Random.nextFloat() < 0.05f
        val redMinutes = if (hasRedCard) generateUniqueMinutes(1, totalMinutes) else emptyList()

        // Distribute home goal details
        homeGoalMinutes.forEach { min ->
            val scorer = selectPlayer(home, listOf(PlayerPosition.FW, PlayerPosition.FW, PlayerPosition.MF))
            val hasAssist = Random.nextFloat() < 0.7f
            val assist = if (hasAssist) selectPlayer(home, listOf(PlayerPosition.MF, PlayerPosition.MF, PlayerPosition.FW, PlayerPosition.DF), exclude = scorer) else null
            
            val comm = if (assist != null) {
                "دقیقه $min: گللللل برای ${home.nameFa}! ${scorer.nameFa} با یک ضربه تماشایی روی پاس گل بی‌نقصِ ${assist.nameFa} دروازه حریف را باز می‌کند!"
            } else {
                "دقیقه $min: گللللل برای ${home.nameFa}! ${scorer.nameFa} با یک ریباند عالی و شوت سرکش توپ را به تور می‌چسباند!"
            }

            events.add(MatchEvent(
                minute = min,
                type = EventType.GOAL,
                teamId = 0,
                teamFa = home.nameFa,
                playerFa = scorer.nameFa,
                playerEn = scorer.nameEn,
                assistFa = assist?.nameFa,
                assistEn = assist?.nameEn,
                commentaryFa = comm
            ))
        }

        // Distribute away goal details
        awayGoalMinutes.forEach { min ->
            val scorer = selectPlayer(away, listOf(PlayerPosition.FW, PlayerPosition.FW, PlayerPosition.MF))
            val hasAssist = Random.nextFloat() < 0.7f
            val assist = if (hasAssist) selectPlayer(away, listOf(PlayerPosition.MF, PlayerPosition.MF, PlayerPosition.FW, PlayerPosition.DF), exclude = scorer) else null

            val comm = if (assist != null) {
                "دقیقه $min: تووووپ وارد دروازه می‌شود! گل برای ${away.nameFa} توسط ${scorer.nameFa} با پاس گل عالی از ${assist.nameFa}."
            } else {
                "دقیقه $min: گل برای ${away.nameFa}! ${scorer.nameFa} با یک فرار تماشایی و شلیک تک‌ضرب، قفل دروازه ${home.nameFa} را می‌شکند."
            }

            events.add(MatchEvent(
                minute = min,
                type = EventType.GOAL,
                teamId = 1,
                teamFa = away.nameFa,
                playerFa = scorer.nameFa,
                playerEn = scorer.nameEn,
                assistFa = assist?.nameFa,
                assistEn = assist?.nameEn,
                commentaryFa = comm
            ))
        }

        // Distribute yellow cards
        yellowMinutes.forEach { min ->
            val isHomeCard = Random.nextBoolean()
            val cardTeam = if (isHomeCard) home else away
            val cardTeamId = if (isHomeCard) 0 else 1
            val player = selectPlayer(cardTeam, listOf(PlayerPosition.DF, PlayerPosition.DF, PlayerPosition.MF))
            
            val comm = "دقیقه $min: کارت زرد 🟨 برای بازیکن ${cardTeam.nameFa}، ${player.nameFa} به دلیل خطای شدید بر روی بازیکن حریف."
            
            val ev = MatchEvent(
                minute = min,
                type = EventType.YELLOW_CARD,
                teamId = cardTeamId,
                teamFa = cardTeam.nameFa,
                playerFa = player.nameFa,
                playerEn = player.nameEn,
                commentaryFa = comm
            )
            events.add(ev)
            yellowCards.add(ev)
        }

        // Distribute red cards
        redMinutes.forEach { min ->
            val isHomeCard = Random.nextBoolean()
            val cardTeam = if (isHomeCard) home else away
            val cardTeamId = if (isHomeCard) 0 else 1
            val player = selectPlayer(cardTeam, listOf(PlayerPosition.DF, PlayerPosition.MF))

            val comm = "دقیقه $min: کارت قرمز مستقیم! 🟥 ${player.nameFa} مدافع ${cardTeam.nameFa} با یک خطای خشن کارت قرمز مستقیم دریافت می‌کند و از زمین اخراج می‌شود!"

            val ev = MatchEvent(
                minute = min,
                type = EventType.RED_CARD,
                teamId = cardTeamId,
                teamFa = cardTeam.nameFa,
                playerFa = player.nameFa,
                playerEn = player.nameEn,
                commentaryFa = comm
            )
            events.add(ev)
            redCards.add(ev)
        }

        // Half time event
        events.add(MatchEvent(
            minute = 45,
            type = EventType.HALF_TIME,
            teamId = 0,
            teamFa = home.nameFa,
            playerFa = "",
            playerEn = "",
            commentaryFa = "سوت پایان نیمه اول! دو تیم برای استراحت و شنیدن صحبت‌های مربیان راهی رختکن می‌شوند."
        ))

        // Near misses for flavor
        val nearMissCount = Random.nextInt(1, 4)
        val nearMissMinutes = generateUniqueMinutes(nearMissCount, totalMinutes)
        nearMissMinutes.forEach { min ->
            val isHomeMiss = Random.nextBoolean()
            val missTeam = if (isHomeMiss) home else away
            val otherTeam = if (isHomeMiss) away else home
            val player = selectPlayer(missTeam, listOf(PlayerPosition.FW, PlayerPosition.MF))
            val gk = selectPlayer(otherTeam, listOf(PlayerPosition.GK))

            val comms = listOf(
                "دقیقه $min: چه موقعیتی! ضربه سر دیدنی ${player.nameFa} با اختلاف بسیار اندک از کنار تیرک عمودی به بیرون می‌رود.",
                "دقیقه $min: وای! شوت سرکش ${player.nameFa} با عکس‌العمل خیره‌کننده ${gk.nameFa} مهار می‌شود و یک کرنر به دست می‌آید.",
                "دقیقه $min: فرصت طلایی تک به تک برای ${player.nameFa} که توپ را مستقیماً به دستان دروازه‌بان می‌کوبد!"
            )

            events.add(MatchEvent(
                minute = min,
                type = EventType.NEAR_MISS,
                teamId = if (isHomeMiss) 0 else 1,
                teamFa = missTeam.nameFa,
                playerFa = player.nameFa,
                playerEn = player.nameEn,
                commentaryFa = comms.random()
            ))
        }

        // Sort events by minute
        events.sortBy { it.minute }

        // Final time event
        val finalComm = if (penaltyShootoutOccurred) {
            "سوت پایان ۱۲۰ دقیقه تلاش نفس‌گیر! بازی با تساوی $homeGoals - $awayGoals پایان می‌یابد و ضربات پنالتی تکلیف برنده را روشن می‌کند. در نهایت ${if (homePens > awayPens) home.nameFa else away.nameFa} با نتیجه $homePens - $awayPens در پنالتی‌ها پیروز شد!"
        } else if (extraTimeOccurred) {
            "پایان ۱۲۰ دقیقه تلاش جانانه! بازی هیجان‌انگیز دو تیم در وقت‌های اضافه با نتیجه $homeGoals - $awayGoals به سود ${if (homeGoals > awayGoals) home.nameFa else away.nameFa} خاتمه می‌یابد."
        } else {
            "سوت پایان بازی! مسابقه دو تیم پس از ۹۰ دقیقه تلاش پرهیجان با نتیجه $homeGoals - $awayGoals به نفع ${if (homeGoals > awayGoals) home.nameFa else if (awayGoals > homeGoals) away.nameFa else "تساوی دو تیم"} به پایان می‌رسد."
        }

        events.add(MatchEvent(
            minute = totalMinutes,
            type = EventType.FULL_TIME,
            teamId = 0,
            teamFa = home.nameFa,
            playerFa = "",
            playerEn = "",
            commentaryFa = finalComm
        ))

        return MatchSimulationResult(
            homeGoals = homeGoals,
            awayGoals = awayGoals,
            extraTime = extraTimeOccurred,
            penalties = penaltyShootoutOccurred,
            homePenalties = homePens,
            awayPenalties = awayPens,
            events = events,
            yellowCards = yellowCards,
            redCards = redCards
        )
    }

    private fun generateGoals(lambda: Double): Int {
        // Poisson approximation using random Knuth algorithm
        val l = Math.exp(-lambda)
        var k = 0
        var p = 1.0
        do {
            k++
            p *= Random.nextDouble()
        } while (p > l)
        return k - 1
    }

    private fun generateUniqueMinutes(count: Int, maxMinutes: Int): List<Int> {
        val minutes = mutableSetOf<Int>()
        var attempts = 0
        while (minutes.size < count && attempts < 100) {
            val min = Random.nextInt(1, maxMinutes + 1)
            // Avoid half-time minute and exact kickoff
            if (min != 45 && min != 90 && min != 120) {
                minutes.add(min)
            }
            attempts++
        }
        return minutes.sorted()
    }

    private fun selectPlayer(team: Team, primaryPositions: List<PlayerPosition>, exclude: Player? = null): Player {
        val candidates = team.players.filter { it != exclude }
        
        // Try to match primary positions first
        val preferred = candidates.filter { it.position in primaryPositions }
        if (preferred.isNotEmpty()) {
            // Weight heavily by rating
            return weightedRandomSelect(preferred)
        }
        
        // Fallback
        return weightedRandomSelect(candidates)
    }

    private fun weightedRandomSelect(players: List<Player>): Player {
        val totalWeight = players.sumOf { it.rating }
        var value = Random.nextInt(totalWeight)
        for (p in players) {
            value -= p.rating
            if (value <= 0) return p
        }
        return players.random()
    }
}

data class MatchSimulationResult(
    val homeGoals: Int,
    val awayGoals: Int,
    val extraTime: Boolean,
    val penalties: Boolean,
    val homePenalties: Int,
    val awayPenalties: Int,
    val events: List<MatchEvent>,
    val yellowCards: List<MatchEvent>,
    val redCards: List<MatchEvent>
)
