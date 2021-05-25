package com.mikolajczak.perpetualcalendar

import java.time.LocalDate

//Funkcja do wyliczania daty Wielkanocy dla danego roku
fun getEasterDate(year: Int): LocalDate {
    val a:Int; val b:Int; val c:Int; val d:Int; val e:Int; val f:Int; val g:Int;
    val h:Int; val i:Int; val k:Int;val l:Int; val m:Int; val p:Int

    val day:Int
    val month: Int

    a = year % 19
    b = year/100
    c = year % 100
    d = b / 4
    e = b % 4
    f = (b+8) / 25
    g = (b-f+1)/3
    h = (19 * a+ b-d-g+15) % 30
    i = c/4
    k = c%4
    l = (32 +2 * e+2*i-h-k) % 7
    m = (a+11*h+22*l)/451
    p = (h+l-7 * m +114) % 31

    day = p +1
    month = (h+l-7 * m +114)/31

    return LocalDate.of(year, month, day)
}

