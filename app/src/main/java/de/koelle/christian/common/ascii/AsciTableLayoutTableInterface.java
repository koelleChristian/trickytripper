package de.koelle.christian.common.ascii;

/**
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++ </p> The original
 * version of this class has been provided by Heiner Kcker via his homepage: <a
 * href=" http://www.heinerkuecker.de/AsciTable.html">
 * http://www.heinerkuecker.de/AsciTable.html</a>
 * <p/>
 * 
 * 
 * The homepage gave the following licence statement (08.06.2012):<blockquote>
 * <q>Die Programme, Quelltexte und Dokumentationen knnen ohne irgendwelche
 * Bedingungen kostenlos verwendet werden. Sie sind Freeware und Open Source.
 * F Fehler und Folgen wird keinerlei Haftung bernommen.</q> </blockquote>
 * 
 * <p>
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++
 * </p>
 * 
 * Interface für Klassen, die mit dem ASCII-Table-Layouter
 * {@link AsciToStringHelper} ausgegeben werden können.
 * 
 * @author Heiner Kcker
 */
public interface AsciTableLayoutTableInterface
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
