package it.cretu.escposprinter.model

/**
 * Represents an EscPos Receipt, basically a Print Queue
 */
class EscPosReceipt {
    private val lines : ArrayList<EscPosLine> = ArrayList()

    /**
     * Retrieves currently added EscPos Lines
     */
    fun getLines() : ArrayList<EscPosLine>{
        return lines
    }

    /**
     * Checks Validity of provided EscPosLine
     */
    private fun checkLine(line : EscPosLine){
        if(line is EscPosTextLine){
            if(line.text.isEmpty())
                throw Exception("EscPosTextLine must have a text value!")
        }
    }

    /**
     * Adds a new EscPos Line to currently stored lines
     */
    fun addLine(line : EscPosLine){
        checkLine(line)
        lines.add(line)
    }

    /**
     * Removes an EscPos Line from currently Stored Lines
     */
    fun removeLine(line : EscPosLine){
        if(lines.contains(line))
            lines.remove(line)
    }

    /**
     * Deletes all Pending Lines
     */
    fun clear(){
        lines.clear()
    }
}