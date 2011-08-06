package net.zutha.lib

case class BaseX(charset: String, baze: Int){
  def encode(value: Int): String = {
    def process(q: Int, place: Int, builder: StringBuilder): StringBuilder = {
      if (q > 0) process(q / baze, place + 1, builder += symbol(q % baze))
      else builder
    }
    if (value == 0) symbol(0).toString
    else process(value, 0, new StringBuilder(32)).reverse.toString
  }

  def decode(str: String): Int = {
    def process(acc: Int, place: Int, str: String, index: Int): Int = {
      if (index >= 0) process(acc + value(str.charAt(index)) * place, place * baze, str, index-1)
      else acc
    }
    process(0, 1, str, str.length - 1)
  }

  private def value(c: Char): Int = charset.indexOf(c)

  private def symbol(i: Int): Char = charset(i)
}
