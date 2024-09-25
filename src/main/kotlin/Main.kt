package com.es.adat01

import java.io.BufferedReader
import java.io.BufferedWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.Path


/*
El fichero cotizacion.csv (que podéis encontrar en la carpeta ficheros) contiene
las cotizaciones de las empresas del IBEX35 con las siguientes columnas:
Nombre (nombre de la empresa),
Final (precio de la acción al cierre de bolsa),
Máximo (precio máximo de la acción durante la jornada),
Mínimo (precio mínimo de la acción durante la jornada),
Volumen (Volumen al cierre de bolsa),
Efectivo (capitalización al cierre en miles de euros).

Construir una función reciba el fichero de cotizaciones y devuelva un diccionario
con los datos del fichero por columnas.

Construir una función que reciba el diccionario devuelto por la función anterior
y cree un fichero en formato csv con el mínimo, el máximo y la media de dada columna.
*/

fun main() {
    val rutaFicheroLectura = Path("src\\main\\resources\\cotizacion.csv")
    val rutaFicheroEscritua = Path("src\\main\\resources\\columnas.csv")
    val diccionario = organizarFicheroDiccionario(rutaFicheroLectura)

    crearFicheroFormato(diccionario, rutaFicheroEscritua)

    diccionario.forEach {
        columna -> println(columna)
    }
}

fun organizarFicheroDiccionario(ruta: Path): MutableMap<String, List<String>> {

    val diccionario: MutableMap<String, List<String>> = mutableMapOf()
    val br: BufferedReader = Files.newBufferedReader(ruta)
    var firstLineRead = false

    val fileLines = mutableListOf<List<String>>()
    val keys = mutableListOf<String>()
    br.use {
        it.forEachLine {
                line ->
            if (firstLineRead) {

                // Usamos un regex para quitar solo los "." que van seguidos de 3 cifras numericas para facilitar el formateo
                val regex = Regex("""\.\d{3}""")
                val lineaFormateada = line.replace(regex) {
                    ""
                }.replace(",", ".")
                val lineaSpliteada: List<String> = lineaFormateada.split(";")
                fileLines.add(lineaSpliteada)

            } else {

                val lineaSpliteada: List<String> = line.split(";")
                lineaSpliteada.forEach { key -> keys.add(key) }
                firstLineRead = true

            }
        }
    }

    keys.forEachIndexed { index, key ->
        val valoresColumna = mutableListOf<String>()
        fileLines.forEach { fila ->
            valoresColumna.add(fila[index])
        }
        diccionario[key] = valoresColumna
    }

    return diccionario
}

fun crearFicheroFormato(diccionario: MutableMap<String, List<String>>, ruta: Path) {

    diccionario.remove("Nombre")

    Files.createDirectories(ruta.parent)

    val bw: BufferedWriter = Files.newBufferedWriter(ruta, StandardOpenOption.CREATE, StandardOpenOption.WRITE)

    bw.use { writer ->

        diccionario.forEach { (key, value) ->
            val doubleList: List<Double> = value.map { it.toDouble() }
            writer.write("Columna ${key}- Mínimo columna: ${doubleList.min()}, Máximo columna: ${doubleList.max()}, Media columna: ${doubleList.average()}")
            writer.newLine()
        }
    }
}