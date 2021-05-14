package main.block

import java.util.*

class BlockPropertyList {
    private val properties: MutableList<Entry> = ArrayList()

    class Entry(val key: String, vararg val values: String)

    /**
     * Returns all possible combinations of properties from this list
     * @return
     */
    val cartesianProduct: List<Array<String>>
        get() {
            if (properties.isEmpty()) {
                return emptyList()
            }
            Collections.sort(properties, Comparator.comparing { a: Entry -> a.key })
            val combinations: MutableList<List<String>> = LinkedList()
            computeCartesianProduct(0, combinations)
            val cartesianProduct: MutableList<Array<String>> = LinkedList()
            for (combination in combinations) {
                cartesianProduct.add(combination.toTypedArray())
            }
            return cartesianProduct
        }

    private fun computeCartesianProduct(currentIndex: Int, out: MutableList<List<String>>) {
        if (currentIndex == properties.size) return
        val current = properties[currentIndex]
        for (value in current.values) {
            val property = current.key + "=" + value
            val cartesianNextProperties: MutableList<List<String>> = LinkedList()
            computeCartesianProduct(currentIndex + 1, cartesianNextProperties)
            if (cartesianNextProperties.isEmpty()) {
                out.add(listOf(property))
            } else {
                for (nextProperties in cartesianNextProperties) {
                    val newList: MutableList<String> = LinkedList()
                    newList.add(property)
                    newList.addAll(nextProperties)
                    out.add(newList)
                }
            }
        }
    }

    /**
     * Defines an int range property. Bounds are inclusive
     * @param key
     * @param rangeStart
     * @param rangeEnd
     * @return
     */
    fun intRange(key: String, rangeStart: Int, rangeEnd: Int): BlockPropertyList {
        assert(rangeStart <= rangeEnd)
        val values = (rangeStart..rangeEnd).toList().map { it.toString() }.toTypedArray()
        return property(key, *values)
    }

    fun property(key: String, vararg values: String): BlockPropertyList {
        properties.add(Entry(key, *values))
        return this
    }

    fun booleanProperty(key: String): BlockPropertyList {
        return property(key, "false", "true")
    }

    fun directionProperty(key: String): BlockPropertyList {
        return property(key, "north", "east", "south", "west", "up", "down")
    }

    fun facingProperty(key: String): BlockPropertyList {
        return property(key, "north", "east", "south", "west")
    }

    val isEmpty: Boolean
        get() = properties.isEmpty()

    fun computeSortedList(): List<Entry> {
        return LinkedList(properties).sortedBy { it.key }
    }
}