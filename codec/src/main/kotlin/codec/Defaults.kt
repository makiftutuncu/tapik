package dev.akif.tapik.codec

interface Defaults<TUnit, TBoolean, TByte, TShort, TInt, TLong, TFloat, TDouble, TBigInteger, TBigDecimal, TString, TUUID> {
    fun unit(name: String): TUnit
    fun boolean(name: String): TBoolean
    fun byte(name: String): TByte
    fun short(name: String): TShort
    fun int(name: String): TInt
    fun long(name: String): TLong
    fun float(name: String): TFloat
    fun double(name: String): TDouble
    fun bigInteger(name: String): TBigInteger
    fun bigDecimal(name: String): TBigDecimal
    fun string(name: String): TString
    fun uuid(name: String): TUUID
}
