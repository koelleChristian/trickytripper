package de.koelle.christian.common.ascii;

/**
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++ </p> The original
 * version of this class has been provided by Heiner Kücker via his homepage: <a
 * href=" http://www.heinerkuecker.de/AsciiTable.html">
 * http://www.heinerkuecker.de/AsciiTable.html</a>
 * <p/>
 * 
 * 
 * The homepage gave the following licence statement (08.06.2012):<blockquote>
 * <q>Die Programme, Quelltexte und Dokumentationen können ohne irgendwelche
 * Bedingungen kostenlos verwendet werden. Sie sind Freeware und Open Source.
 * Für Fehler und Folgen wird keinerlei Haftung übernommen.</q> </blockquote>
 * 
 * <p>
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++
 * </p>
 * 
 * Interface für Klassen, die mit dem ASCII-Table-Layouter
 * {@link AsciToStringHelper} ausgegeben werden können.
 * 
 * @author Heiner Kücker
 */
public interface AsciTableLayoutableInterface
{
    /**
     * Names of Columns.
     * 
     * @return Names of Columns
     */
    public String[] asciTableColumnNames();

    /**
     * content of cell for the given row number and column number.
     * 
     * @return number of cell
     */
    public Object asciTableColumnContent(// --
            final int pColIndex);
}
