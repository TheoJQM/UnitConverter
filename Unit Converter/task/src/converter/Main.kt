package converter

/**
 * Enum representing various units of measurement, including length, weight, and temperature.
 */
enum class Units(val notations: List<String>, val type: String, val conversionRate:Double) {
    Meters(listOf("m", "meter","meters")  , "Length", 1.0),
    Kilometers( listOf("km", "kilometer", "kilometers"), "Length", 1000.0),
    Centimeters(listOf("cm", "centimeter", "centimeters"), "Length", 0.01),
    Millimeters(listOf("mm", "millimeter", "millimeters"), "Length", 0.001),
    Miles(listOf("mi", "mile", "miles"), "Length", 1609.35),
    Yards(listOf("yd", "yard", "yards"), "Length", 0.9144),
    Feet(listOf("ft", "foot", "feet"), "Length", 0.3048),
    Inches(listOf("in", "inch", "inches"), "Length", 0.0254),
    Grams(listOf("g", "gram", "grams"), "Weight ", 1.0),
    Kilograms(listOf("kg", "kilogram", "kilograms"), "Weight ", 1000.0),
    Milligrams(listOf("mg",  "milligram", "milligrams"), "Weight ", 0.001),
    Pounds(listOf("lb", "pound", "pounds"), "Weight ", 453.592),
    Ounces(listOf("oz", "ounce", "ounces"), "Weight ", 28.3495),
    Celsius(listOf("c", "dc",  "celsius", "degree Celsius", "degrees Celsius"), "Temperature", 0.0),
    Fahrenheit(listOf("f", "df", "fahrenheit", "degree Fahrenheit", "degrees Fahrenheit"), "Temperature", 0.0),
    Kelvin(listOf("k", "kelvin", "kelvins"), "Temperature", 0.0),
    Else(listOf(""), "", 0.0)

}

class Converter {
    private var inputSource = Pair(0.0, "")
    private var inputTarget = ""
    private val query = "(-)?[0-9]+(.[0-9]+)? ([a-zA-Z]+ )+([a-zA-Z]*to[a-zA-Z]*|[a-zA-Z]*in[a-zA-Z]*) ([a-zA-Z]+|[a-zA-Z]+ [a-zA-Z]+)".toRegex()

    fun start() {
        while (true) {
            print("Enter what you want to convert (or exit): ")
            val inputs = readln().split(" ")
            if (inputs.first() == "exit") break
            processUserInput(inputs)
        }
    }

    /**
     * Processes user input for a conversion query, validates it, and performs the conversion if valid.
     * @param inputs List of the user inputs.
     */
    private fun processUserInput(inputs: List<String>) {
        val isValidQuery = validateQuery(inputs)

        val unitsSource = getUnits(inputSource.second)
        val unitsTarget = getUnits(inputTarget)

        when {
            !isValidQuery -> return
            !validateUnits(unitsSource, unitsTarget) -> return
            !validateAmount(unitsSource) -> return
            unitsSource.type != unitsTarget.type -> {
                println("Conversion from ${unitsSource.notations.last()} to ${unitsTarget.notations.last()} is impossible\n")
            }
            else -> {
                convert(unitsSource, unitsTarget)
            }
        }
    }

    /**
     * Validates the user input for a conversion query and updates the inputSource and inputTarget accordingly.
     * @param inputs List of the user inputs.
     *
     * @return True if the input matches the specified regex pattern, false otherwise.
     */
    private fun validateQuery(inputs: List<String>): Boolean {
        if (inputs.joinToString(" ").lowercase().matches(query)) {
            when (inputs.size) {
                4 -> {
                    inputSource = inputSource.copy(inputs[0].toDouble(), inputs[1])
                    inputTarget = inputs[3]
                }
                6 -> {
                    inputSource = inputSource.copy(inputs[0].toDouble(), inputs[2])
                    inputTarget = inputs[5]
                }
                else -> {
                    inputSource = if (inputs[inputs.lastIndex - 1] matches  "to|in".toRegex()) {
                        inputSource.copy(inputs[0].toDouble(), inputs[2])
                    } else {
                        inputSource.copy(inputs[0].toDouble(), inputs[1])
                    }
                    inputTarget = inputs[inputs.lastIndex]
                }
            }
            return true
        } else {
            println("Parse Error\n")
            return false
        }
    }

    /**
     * Retrieves the Units enum corresponding to the provided input notation.
     * @param input : The input notation for the unit.
     *
     * @return The Units enum corresponding to the input notation, or Units.Else if not found.
     */
    private fun getUnits(input : String): Units {
        Units.values().forEach { unit ->
            if (unit.notations.map { it.lowercase() }.contains(input.lowercase())) return unit
        }
        return Units.Else
    }

    /**
     * Checks if the input provided by the user is not below zero.
     * (Applicable only for length and weight conversions.)
     *
     * @param unitSource : The unit associated with the input value.
     * @return `true` if the input value is above zero, `false` if it's below zero.
     */
    private fun validateAmount(unitSource: Units): Boolean {
        if (inputSource.first < 0 && unitSource.type != "Temperature") {
            println("${unitSource.type} shouldn't be negative\n")
            return false
        }
        return true
    }

    /**
     * Checks if the user provided correct units for the conversion.
     * @param unitSource : The unit associated with the source value.
     * @param unitTarget : The unit to which the value needs to be converted.
     *
     * @return `true` if the units are correct, `false` if the conversion is not possible.
     */
    private fun validateUnits(unitSource : Units, unitTarget: Units): Boolean {
        val unitS = if (unitSource.notations.first() == "") "???" else unitSource.notations.last()
        val unitT = if (unitTarget.notations.first() == "") "???" else unitTarget.notations.last()

        if (unitS == "???" || unitT == "???") {
            println("Conversion from $unitS to $unitT is impossible\n")
            return false
        }
        return true
    }

    /**
     * Converts the value given by the user with the source unit to the target unit
     * @param unitSource : The value and the unit to convert.
     * @param unitTarget : The unit to which the value needs to be converted.
     */
    private fun convert(unitSource: Units, unitTarget: Units) {
        if (unitSource.type == "Temperature") {
            convertTemperature(unitSource, unitTarget)
        } else {
            val convertedInputSource = inputSource.first * unitSource.conversionRate
            val convertedInputTarget = unitTarget.conversionRate

            val convertedValue = convertedInputSource / convertedInputTarget
            println("${inputSource.first} ${if (inputSource.first == 1.0) unitSource.notations[unitSource.notations.lastIndex - 1] else unitSource.notations.last()} is" +
                    " $convertedValue ${if (convertedValue == 1.0) unitTarget.notations[unitTarget.notations.lastIndex - 1] else unitTarget.notations.last()}\n")
        }
    }

    /**
     * Converts the temperature provided by the user (in °C, °F, or °K).
     * @param unitSource : The temperature to be converted.
     * @param unitTarget : The target temperature unit for the conversion.
     */
    private fun convertTemperature(unitSource: Units, unitTarget: Units) {
        val sourceUnit = unitSource.notations[unitSource.notations.lastIndex - 1]
        val targetUnit = unitTarget.notations[unitTarget.notations.lastIndex - 1]

        val result: Double = when  {
            sourceUnit == "degree Celsius" && targetUnit == "degree Fahrenheit" -> celsiusFahrenheit(inputSource.first)
            sourceUnit == "degree Fahrenheit" && targetUnit == "degree Celsius" -> fahrenheitCelsius(inputSource.first)

            sourceUnit == "degree Celsius" && targetUnit == "kelvin" -> celsiusKelvin(inputSource.first)
            sourceUnit == "kelvin" && targetUnit == "degree Celsius" -> kelvinCelsius(inputSource.first)

            sourceUnit == "degree Fahrenheit" && targetUnit == "kelvin" -> fahrenheitKelvin(inputSource.first)
            sourceUnit == "kelvin" && targetUnit == "degree Fahrenheit" -> kelvinFahrenheit(inputSource.first)

            else -> inputSource.first
        }

        println("${inputSource.first} ${if (inputSource.first == 1.0) unitSource.notations[unitSource.notations.lastIndex - 1] else unitSource.notations.last()} is" +
                " $result ${if (result == 1.0) unitTarget.notations[unitTarget.notations.lastIndex - 1] else unitTarget.notations.last()}\n")
    }


    /*
 * All the functions below are used in temperature conversion:
 * From Celsius to Fahrenheit, to Kelvin to Celsius, to Fahrenheit to Kelvin...
 */

    /**
     * Converts temperature from Celsius to Fahrenheit.
     */
    private fun celsiusFahrenheit(number: Double): Double = number * 9 / 5 + 32

    /**
     * Converts temperature from Fahrenheit to Celsius.
     */
    private fun fahrenheitCelsius(number: Double): Double = (number - 32) * 5/9

    /**
     * Converts temperature from Celsius to Kelvin.
     */
    private fun celsiusKelvin(number: Double): Double = number + 273.15

    /**
     * Converts temperature from Kelvin to Celsius.
     */
    private fun kelvinCelsius(number: Double): Double = number - 273.15

    /**
     * Converts temperature from Fahrenheit to Kelvin.
     */
    private fun fahrenheitKelvin(number: Double): Double = (number + 459.67) * 5 / 9

    /**
     * Converts temperature from Kelvin to Fahrenheit.
     */
    private fun kelvinFahrenheit(number: Double): Double = number * 9 / 5 - 459.67
}

fun main() {
    val converter = Converter()
    converter.start()
}