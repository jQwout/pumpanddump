package shared

class BoundedQueue<T>(val maxSize: Int) {
    private val queue = ArrayDeque<T>()

    fun add(item: T) {
        if (queue.size >= maxSize) {
            queue.removeFirst() // Удаляем самый старый элемент
        }
        queue.addLast(item) // Добавляем в конец, сохраняя хронологию
    }

    fun getAll(): List<T> = queue.toList()

    override fun toString(): String = queue.toString()
}