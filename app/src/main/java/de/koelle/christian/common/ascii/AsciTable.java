package de.koelle.christian.common.ascii;

import java.util.ArrayList;

/**
 * <p>
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++
 * </p>
 * The original version of this class has been provided by Heiner K�cker via his
 * homepage: <a href=" http://www.heinerkuecker.de/AsciTable.html">
 * http://www.heinerkuecker.de/AsciTable.html</a>
 * <p/>
 * 
 * 
 * The homepage gave the following licence statement (08.06.2012):<blockquote>
 * <q>Die Programme, Quelltexte und Dokumentationen k�nnen ohne irgendwelche
 * Bedingungen kostenlos verwendet werden. Sie sind Freeware und Open Source.
 * F�r Fehler und Folgen wird keinerlei Haftung �bernommen.</q> </blockquote>
 * 
 * <p>
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++
 * </p>
 * 
 * Diese Klasse dient zum Layouten einer Tabelle in ASCII-Art . <br>
 * 
 * Durch den rekursiven Aufbau ist die Darstellung verschachtelter Tabellen
 * m�glich.<br>
 * 
 * <u>AsciTable</u><br>
 * <br>
 * 
 * Diese kleine Klasse bietet einen Table-Layouter der sich an die HTML-Tabellen
 * anlehnt, aber mit den bekannten ASCII-Zeichen statt mit grafischer
 * Darstellung arbeitet.<br>
 * <br>
 * 
 * Nun wird man sich fragen, wozu man so etwas im 21. Jahrhundert noch ben�tigt.
 * Zum einen wollte ich sowas einfach mal programmieren. Andererseits steht beim
 * Logging, Debugging und Fehler-Tracking oft keine grafische Oberfl�che zur
 * Verf�gung, sondern reine Textausgaben.<br>
 * <br>
 * 
 * Mit dieser kleinen Klasse lassen sich tabellarische Daten in Textform
 * halbwegs �bersichtlich darstellen.<br>
 * <br>
 * 
 * Interessant ist die M�glichkeit hierachische Daten durch das Verschachteln
 * der Tabellen abzubilden.<br>
 * <br>
 * 
 * Es gibt Util-Methoden zum Anf�gen von Kopf- und Fuss-Zeilen, einer Spalte mit
 * laufenden Nummern und <b>colspan</b> zum �berspannen mehrerer Spalten.<br>
 * <br>
 * 
 * <b>rowspan</b> zum �berspannen mehrerer Zeilen ist (noch) nicht
 * implementiert. Das habe ich bisher nicht ben�tigt und ist auch etwas
 * komplizierter.<br>
 * <br>
 * 
 * Falls <b>colspan</b> nicht ben�tigt wird, sollten die buildLayout()-Methoden
 * ohne <b>colspan</b> verwendet werden, da diese schneller und wahrscheinlich
 * auch sicherer (fehlerstabiler) sind.<br>
 * <br>
 * 
 * Die Tabellen-Trennlinien lassen sich folgendermassen beeinflussen:
 * 
 * <pre>
 * +===+===+===+===+  Kopflinie  (keine, Leerzeichen, einfach, doppelt)
 * | 00| 01| 02| 03|
 * +---+---+---+---+  Trennlinie (keine, Leerzeichen, einfach, doppelt)
 * |100|101|102|103|
 * +===+===+===+===+  Fusslinie  (keine, Leerzeichen, einfach, doppelt)
 * 
 * oder
 * 
 * +===+===+===+===+  Kopflinie  (keine, Leerzeichen, einfach, doppelt)
 * | 00| 01| 02| 03|
 * +===+===+===+===+  Kopf-Trennlinie (keine, Leerzeichen, einfach, doppelt)
 * |100|101|102|103|
 * +---+---+---+---+  Trennlinie (keine, Leerzeichen, einfach, doppelt)
 * |200|201|202|203|
 * +===+===+===+===+  Fuss-Trennlinie (keine, Leerzeichen, einfach, doppelt)
 * |300|301|302|303|
 * +===+===+===+===+  Fusslinie  (keine, Leerzeichen, einfach, doppelt)
 * </pre>
 * 
 * Falls jemand einen Fehler findet, w�rde ich mich �ber einen entsprechenden
 * Hinweis per e-mail freuen.<br>
 * <br>
 * 
 * <br/>
 * <b>Author:</b> <a href="http://www.heinerkuecker.de" target="_blank">Heiner
 * K�cker</a><br/>
 * <br/>
 */
@SuppressWarnings("ALL")
public class AsciTable
{
    /** Zellen-Array */
    private final Cell[][] tableCellArrArr;

    /** Betriebsart Kopf-Linie */
    private int kopfMode = SINGLE_LINE;

    /** Betriebsart Kopf-Trenn-Linie */
    private int kopfTrennMode = DOUBLE_LINE;

    /** Betriebsart Trenn-Linie */
    private int trennMode = SINGLE_LINE;

    /** Betriebsart Fuss-Linie */
    private int fussTrennMode = SINGLE_LINE;

    /** Betriebsart Fuss-Linie */
    private int fussMode = SINGLE_LINE;

    // -------------------------------------------------
    // ------------ Betriebsarten Linien ---------------
    // -------------------------------------------------

    /**
     * keine Trennlinie . <br>
     * value = 0
     */
    public static final int NO_LINE = 0;

    /**
     * leere Trennlinie . <br>
     * value = 1
     */
    public static final int EMPTY_LINE = 1;

    /**
     * einfache Trennlinie . <br>
     * value = 2
     */
    public static final int SINGLE_LINE = 2;

    /**
     * doppelte Trennlinie . <br>
     * value = 3
     */
    public static final int DOUBLE_LINE = 3;

    // ----------------------------------------------------------
    // ------------ Betriebsarten Zellen-Ausrichtung ------------
    // ----------------------------------------------------------

    /**
     * Ausrichtung belassen . <br>
     * value = 0
     */
    public static final int NO_ALIGNMENT = 0;

    /**
     * Ausrichtung linksb�ndig . <br>
     * value = 1
     */
    public static final int LEFT_ALIGNMENT = 1;

    /**
     * Ausrichtung rechtsb�ndig . <br>
     * value = 2
     */
    public static final int RIGHT_ALIGNMENT = 2;

    /** Ausrichtung zentriert noch nicht implementiert */
    // public static final int CENTER_ALIGNMENT = 3;

    // --------------------
    // get set
    // --------------------

    /**
     * @return Returns the fussMode.
     */
    public int getFussMode()
    {
        return this.fussMode;
    }

    /**
     * @param pFussMode
     *            The fussMode to set.
     */
    public void setFussMode(int pFussMode)
    {
        this.fussMode = pFussMode;
    }

    /**
     * @return Returns the fussTrennMode.
     */
    public int getFussTrennMode()
    {
        return this.fussTrennMode;
    }

    /**
     * @param pFussTrennMode
     *            The fussTrennMode to set.
     */
    public void setFussTrennMode(int pFussTrennMode)
    {
        this.fussTrennMode = pFussTrennMode;
    }

    /**
     * @return Returns the kopfMode.
     */
    public int getKopfMode()
    {
        return this.kopfMode;
    }

    /**
     * @param pKopfMode
     *            The kopfMode to set.
     */
    public void setKopfMode(int pKopfMode)
    {
        this.kopfMode = pKopfMode;
    }

    /**
     * @return Returns the kopfTrennMode.
     */
    public int getKopfTrennMode()
    {
        return this.kopfTrennMode;
    }

    /**
     * @param pKopfTrennMode
     *            The kopfTrennMode to set.
     */
    public void setKopfTrennMode(int pKopfTrennMode)
    {
        this.kopfTrennMode = pKopfTrennMode;
    }

    /**
     * @return Returns the trennMode.
     */
    public int getTrennMode()
    {
        return this.trennMode;
    }

    /**
     * @param pTrennMode
     *            The trennMode to set.
     */
    public void setTrennMode(int pTrennMode)
    {
        this.trennMode = pTrennMode;
    }

    // -------------------------------------------------------------------
    // Konstruktor
    // -------------------------------------------------------------------
    /**
     * Constructor .
     * 
     * @param pColCount
     *            Anzahl Spalten
     * @param pRowCount
     *            Anzahl Zeilen
     */
    public AsciTable( // --
            final int pColCount, // --
            final int pRowCount)
    {
        this.tableCellArrArr = new Cell[pColCount][pRowCount];
    }

    /**
     * Constructor .
     * 
     * @param pTableCellArrArr
     * @param pKopfMode
     * @param pKopfTrennMode
     * @param pTrennMode
     * @param pFussTrennMode
     * @param pFussMode
     */
    public AsciTable(
            final int pColCount,
            final int pRowCount,
            final int pKopfMode,
            final int pKopfTrennMode,
            final int pTrennMode,
            final int pFussTrennMode,
            final int pFussMode)
    {
        this.tableCellArrArr = new Cell[pColCount][pRowCount];
        this.kopfMode = pKopfMode;
        this.kopfTrennMode = pKopfTrennMode;
        this.trennMode = pTrennMode;
        this.fussTrennMode = pFussTrennMode;
        this.fussMode = pFussMode;
    }

    // -----------------------------------------------------------------------------------------
    //
    // Methoden zum Setzen der Zellen
    //
    // -----------------------------------------------------------------------------------------

    /**
     * 
     * 
     * @param pCol
     * @param pRow
     * @param pStr
     */
    public void set( // --
            final int pCol, // --
            final int pRow, // --
            final Cell pCell)
    {
        this.tableCellArrArr[pCol][pRow] = pCell;
    }

    /**
     * 
     * 
     * @param pCol
     * @param pRow
     * @param pStr
     */
    public void set( // --
            final int pCol, // --
            final int pRow, // --
            final String pStr)
    {
        Cell cell = this.tableCellArrArr[pCol][pRow];
        if (cell == null)
        {
            this.tableCellArrArr[pCol][pRow] = new Cell(pStr);
        }
        else
        {
            cell.contentLineArr = splitForNewline(pStr);
        }
    }

    /**
     * 
     * 
     * @param pCol
     * @param pRow
     * @param pStr
     */
    public void set( // --
            final int pCol, // --
            final int pRow, // --
            final Number pNum)
    {
        set(pCol, pRow, "" + pNum);
        this.tableCellArrArr[pCol][pRow].alignment = RIGHT_ALIGNMENT;
    }

    /**
     * 
     * 
     * @param pCol
     * @param pRow
     * @param pStr
     */
    public void setAlignment( // --
            final int pCol, // --
            final int pRow, // --
            final int pAlignment)
    {
        Cell cell = this.tableCellArrArr[pCol][pRow];
        if (cell == null)
        {
            cell = new Cell("");
            this.tableCellArrArr[pCol][pRow] = cell;
        }
        cell.alignment = pAlignment;
    }

    // -----------------------------------------------------------------------------------------
    //
    // Build-Methoden ohne colspan
    //
    // -----------------------------------------------------------------------------------------

    /**
     * Diese Methode erzeugt aus dem �bergebenen 2-dimensionalen Zellen-Array
     * einen umgebrochenen Ausgabe-String mit dem Aussehen der gew�nschten
     * Tabelle . <br>
     * <br>
     * Die Tabellen-Trennlinien lassen sich folgendermassen beeinflussen:
     * 
     * <pre>
     * +===+===+===+===+  Kopflinie  (keine, Leerzeichen, einfach, doppelt)
     * | 00| 01| 02| 03|
     * +---+---+---+---+  Trennlinie (keine, Leerzeichen, einfach, doppelt)
     * |100|101|102|103|
     * +===+===+===+===+  Fusslinie  (keine, Leerzeichen, einfach, doppelt)
     * </pre>
     * 
     * @param pTableCellArrArr
     * @return String mit Umbr�chen mit layouteter Tabelle
     * @see #NO_LINE
     * @see #EMPTY_LINE
     * @see #SINGLE_LINE
     * @see #DOUBLE_LINE
     */
    public static String buildLayout( // -
            Cell[][] pTableCellArrArr, // Zellen-Array
            int pKopfMode, // Betriebsart Kopf-Linie
            int pTrennMode, // Betriebsart Trenn-Linie
            int pFussMode) // Betriebsart Fuss-Linie
    {
        String retStr = buildLayout( // -
                pTableCellArrArr, // Zellen-Array
                pKopfMode, // Betriebsart Kopf-Linie
                pTrennMode, // Betriebsart Kopf-Linie
                pTrennMode, // Betriebsart Trenn-Linie
                pTrennMode, // Betriebsart Fuss-Linie
                pFussMode); // Betriebsart Fuss-Linie

        return retStr;
    }

    /**
     * Diese Methode erzeugt aus dem �bergebenen 2-dimensionalen Zellen-Array
     * einen umgebrochenen Ausgabe-String mit dem Aussehen der gew�nschten
     * Tabelle.
     * 
     * <br>
     * Die Tabellen-Trennlinien lassen sich folgendermassen beeinflussen:
     * 
     * <pre>
     * +===+===+===+===+  Kopflinie  (keine, Leerzeichen, einfach, doppelt)
     * | 00| 01| 02| 03|
     * +===+===+===+===+  Kopf-Trennlinie (keine, Leerzeichen, einfach, doppelt)
     * |100|101|102|103|
     * +---+---+---+---+  Trennlinie (keine, Leerzeichen, einfach, doppelt)
     * |200|201|202|203|
     * +===+===+===+===+  Fuss-Trennlinie (keine, Leerzeichen, einfach, doppelt)
     * |300|301|302|303|
     * +===+===+===+===+  Fusslinie  (keine, Leerzeichen, einfach, doppelt)
     * </pre>
     * 
     * Bei Aufruf dieser Methode muss die Tabelle mindestens drei Zeilen
     * besitzen.
     * 
     * @param pTableCellArrArr
     * @return String mit Umbr�chen mit layouteter Tabelle
     * @see #NO_LINE
     * @see #EMPTY_LINE
     * @see #SINGLE_LINE
     * @see #DOUBLE_LINE
     */
    public static String buildLayout( // -
            Cell[][] pTableCellArrArr, // Zellen-Array
            int pKopfMode, // Betriebsart Kopf-Linie
            int pKopfTrennMode, // Betriebsart Kopf-Linie
            int pTrennMode, // Betriebsart Trenn-Linie
            int pFussTrennMode, // Betriebsart Fuss-Linie
            int pFussMode) // Betriebsart Fuss-Linie
    {
        // ---------------------------------------------------
        // Auslayouten:
        // maximale H�he der Zellen einer Zeile bestimmen
        // maximale Breite der Zellen einer Spalte bestimmen
        // ---------------------------------------------------
        int[] maxHeightArr = new int[pTableCellArrArr.length];
        int[] maxWidthArr = new int[pTableCellArrArr[0].length];

        computeMaxHeightWidth(pTableCellArrArr, maxHeightArr, maxWidthArr);

        // ------------------------------------------
        // Aufbauen auslayoutete Tabelle
        // ------------------------------------------
        StringBuffer strBuff = new StringBuffer();

        final String leerTrennLinienStr = createLeerTrennLinie(maxWidthArr);
        final String einfachTrennLinienStr = createEinfachTrennLinie(maxWidthArr);
        final String doppelTrennlinienStr = createDoppelTrennLinie(maxWidthArr);

        // obere Rand-Linie
        appendLine(strBuff, pKopfMode, leerTrennLinienStr, einfachTrennLinienStr,
                doppelTrennlinienStr);

        if (pKopfMode > NO_LINE)
        {
            strBuff.append('\n');
        }

        // for �ber Zeilen
        for (int iRow = 0; iRow < pTableCellArrArr.length; iRow++)
        {
            // for �ber Text-Zeilen je Tabellen-Zeile
            for (int iCellLine = 0; iCellLine < maxHeightArr[iRow]; iCellLine++)
            {
                strBuff.append("|"); // links
                // for �ber Spalten
                for (int iCol = 0; iCol < pTableCellArrArr[iRow].length; iCol++)
                {
                    if (iCol > 0)
                    {
                        strBuff.append("|"); // zwischen
                    }
                    String cellStr;

                    if (pTableCellArrArr[iRow][iCol] != null)
                    {
                        cellStr = pTableCellArrArr[iRow][iCol].getLine(iCellLine,
                                maxWidthArr[iCol]);
                    }
                    else
                    {
                        cellStr = strAlign("", maxWidthArr[iCol]);
                    }

                    strBuff.append(cellStr);

                }
                strBuff.append("|\n"); // rechts
            }
            // Linie unmittelbar unter dem Kopf
            // es muss mindestens drei Linien geben
            if (iRow == 0 && pTableCellArrArr.length > 2)
            {
                appendLine(strBuff, pKopfTrennMode, leerTrennLinienStr, einfachTrennLinienStr,
                        doppelTrennlinienStr);
                if (pKopfTrennMode > NO_LINE)
                {
                    strBuff.append('\n');
                }

            }
            // Linie vor der letzten Zeile
            // es muss mindestens drei Linien geben
            else if (iRow == pTableCellArrArr.length - 2 && pTableCellArrArr.length > 2)
            {
                appendLine(strBuff, pFussTrennMode, leerTrennLinienStr, einfachTrennLinienStr,
                        doppelTrennlinienStr);

                if (pFussTrennMode > NO_LINE)
                {
                    strBuff.append('\n');
                }
            }
            else if (iRow < pTableCellArrArr.length - 1)
            {
                // nicht letzter Schleifendurchlauf
                // Trenn-Linie zwischen den Zeilen
                appendLine(strBuff, pTrennMode, leerTrennLinienStr, einfachTrennLinienStr,
                        doppelTrennlinienStr);

                if (pTrennMode > NO_LINE)
                {
                    strBuff.append('\n');
                }
            }
        }

        // Fuss-Linie
        appendLine(strBuff, pFussMode, leerTrennLinienStr, einfachTrennLinienStr,
                doppelTrennlinienStr);

        if (pFussMode > NO_LINE)
        {
            strBuff.append('\n');
        }
        return strBuff.toString();
    }

    /**
     * Auslayouten: maximale H�he der Zellen einer Zeile bestimmen maximale
     * Breite der Zellen einer Spalte bestimmen
     * 
     * @param pTableCellArrArr
     * @param pMaxHeightArr
     * @param pMaxWidthArr
     */
    private static void computeMaxHeightWidth(
            Cell[][] pTableCellArrArr,
            int[] pMaxHeightArr,
            int[] pMaxWidthArr)
    {
        // Mindesth�he 1 einstellen
        for (int i = 0; i < pMaxHeightArr.length; i++)
        {
            pMaxHeightArr[i] = 1;
        }

        for (int iRow = 0; iRow < pTableCellArrArr.length; iRow++)
        {
            for (int iCol = 0; iCol < pTableCellArrArr[iRow].length; iCol++)
            {
                if (pTableCellArrArr[iRow][iCol] != null)
                {
                    // Vermerken maximale H�he
                    pMaxHeightArr[iRow] = Math.max(pMaxHeightArr[iRow],
                            pTableCellArrArr[iRow][iCol].getHeight());

                    // Vermerken maximale Breite
                    pMaxWidthArr[iCol] = Math.max(pMaxWidthArr[iCol],
                            pTableCellArrArr[iRow][iCol].getWidth());
                }
            }
        }
    }

    // -----------------------------------------------------------------------------------------
    //
    // Build-Methoden mit colspan
    //
    // -----------------------------------------------------------------------------------------

    /**
     * Diese Methode erzeugt aus dem �bergebenen 2-dimensionalen Zellen-Array
     * einen umgebrochenen Ausgabe-String mit dem Aussehen der gew�nschten
     * Tabelle . <br>
     * <br>
     * Die Tabellen-Trennlinien lassen sich folgendermassen beeinflussen:
     * 
     * <pre>
     * +===+===+===+===+  Kopflinie  (keine, Leerzeichen, einfach, doppelt)
     * | 00| 01| 02| 03|
     * +---+---+---+---+  Trennlinie (keine, Leerzeichen, einfach, doppelt)
     * |100|101|102|103|
     * +===+===+===+===+  Fusslinie  (keine, Leerzeichen, einfach, doppelt)
     * </pre>
     * 
     * @param pTableCellArrArr
     * @return String mit Umbr�chen mit layouteter Tabelle
     * @see #NO_LINE
     * @see #EMPTY_LINE
     * @see #SINGLE_LINE
     * @see #DOUBLE_LINE
     */
    public static String buildLayoutColspan( // -
            Cell[][] pTableCellArrArr, // Zellen-Array
            int pKopfMode, // Betriebsart Kopf-Linie
            int pTrennMode, // Betriebsart Trenn-Linie
            int pFussMode) // Betriebsart Fuss-Linie
    {
        String retStr = buildLayoutColspan( // -
                pTableCellArrArr, // Zellen-Array
                pKopfMode, // Betriebsart Kopf-Linie
                pTrennMode, // Betriebsart Kopf-Linie
                pTrennMode, // Betriebsart Trenn-Linie
                pTrennMode, // Betriebsart Fuss-Linie
                pFussMode); // Betriebsart Fuss-Linie

        return retStr;
    }

    /**
     * Diese Methode erzeugt aus dem �bergebenen 2-dimensionalen Zellen-Array
     * einen umgebrochenen Ausgabe-String mit dem Aussehen der gew�nschten
     * Tabelle.
     * 
     * <br>
     * Die Tabellen-Trennlinien lassen sich folgendermassen beeinflussen:
     * 
     * <pre>
     * +===+===+===+===+  Kopflinie  (keine, Leerzeichen, einfach, doppelt)
     * | 00| 01| 02| 03|
     * +===+===+===+===+  Kopf-Trennlinie (keine, Leerzeichen, einfach, doppelt)
     * |100|101|102|103|
     * +---+---+---+---+  Trennlinie (keine, Leerzeichen, einfach, doppelt)
     * |200|201|202|203|
     * +===+===+===+===+  Fuss-Trennlinie (keine, Leerzeichen, einfach, doppelt)
     * |300|301|302|303|
     * +===+===+===+===+  Fusslinie  (keine, Leerzeichen, einfach, doppelt)
     * </pre>
     * 
     * Bei Aufruf dieser Methode muss die Tabelle mindestens drei Zeilen
     * besitzen.
     * 
     * @param pTableCellArrArr
     * @return String mit Umbr�chen mit layouteter Tabelle
     * @see #NO_LINE
     * @see #EMPTY_LINE
     * @see #SINGLE_LINE
     * @see #DOUBLE_LINE
     */
    public static String buildLayoutColspan( // -
            Cell[][] pTableCellArrArr, // Zellen-Array
            int pKopfMode, // Betriebsart Kopf-Linie
            int pKopfTrennMode, // Betriebsart Kopf-Linie
            int pTrennMode, // Betriebsart Trenn-Linie
            int pFussTrennMode, // Betriebsart Fuss-Linie
            int pFussMode) // Betriebsart Fuss-Linie
    {
        // ---------------------------------------------------
        // Auslayouten:
        // maximale H�he der Zellen einer Zeile bestimmen
        // maximale Breite der Zellen einer Spalte bestimmen
        // ---------------------------------------------------
        int[] maxHeightArr = new int[pTableCellArrArr.length];
        int[] maxWidthArr = new int[pTableCellArrArr[0].length];

        computeMaxHeightWidthColspan(pTableCellArrArr, maxHeightArr, maxWidthArr);

        // ------------------------------------------
        // Aufbauen auslayoutete Tabelle
        // ------------------------------------------
        StringBuffer strBuff = new StringBuffer();

        final String leerTrennLinienStr = createLeerTrennLinie(maxWidthArr);
        final String einfachTrennLinienStr = createEinfachTrennLinie(maxWidthArr);
        final String doppelTrennlinienStr = createDoppelTrennLinie(maxWidthArr);

        // obere Rand-Linie
        appendLine(strBuff, pKopfMode, leerTrennLinienStr, einfachTrennLinienStr,
                doppelTrennlinienStr);

        if (pKopfMode > NO_LINE)
        {
            strBuff.append('\n');
        }

        // for �ber Zeilen
        for (int iRow = 0; iRow < pTableCellArrArr.length; iRow++)
        {
            // for �ber Text-Zeilen je Tabellen-Zeile
            for (int iCellLine = 0; iCellLine < maxHeightArr[iRow]; iCellLine++)
            {
                strBuff.append("|"); // links
                // for �ber Spalten
                int iColspanOffset = 0;

                for (int iCol = 0; iColspanOffset < maxWidthArr.length
                        && iCol < pTableCellArrArr[iRow].length; iCol++)
                {
                    if (iCol > 0)
                    {
                        strBuff.append("|"); // zwischen
                    }
                    String cellStr;

                    if (pTableCellArrArr[iRow][iCol] != null)
                    {
                        // Aufsammeln der Breiten
                        int colspanWidth = 0;

                        for (int iColspan = 0; iColspan
                        < pTableCellArrArr[iRow][iCol].getColspan(); iColspan++)
                        {
                            if (iColspan > 0)
                            {
                                // L�cke mitz�hlen
                                colspanWidth++;
                            }
                            final int colspanWidthOffset = iColspan + iColspanOffset;
                            final int colspaOffsWidth = maxWidthArr[colspanWidthOffset];

                            colspanWidth += colspaOffsWidth;
                        }

                        cellStr = pTableCellArrArr[iRow][iCol].getLine(iCellLine, colspanWidth);
                        // um den colspan weiterschalten
                        iColspanOffset += pTableCellArrArr[iRow][iCol].getColspan();

                    }
                    else
                    {
                        cellStr = strAlign("", maxWidthArr[iColspanOffset]);
                    }
                    strBuff.append(cellStr);
                }
                strBuff.append("|\n"); // rechts
            }
            // Linie unmittelbar unter dem Kopf
            // es muss mindestens drei Linien geben
            if (iRow == 0 && pTableCellArrArr.length > 2)
            {
                appendLine(strBuff, pKopfTrennMode, leerTrennLinienStr, einfachTrennLinienStr,
                        doppelTrennlinienStr);
                if (pKopfTrennMode > NO_LINE)
                {
                    strBuff.append('\n');
                }

            }
            // Linie vor der letzten Zeile
            // es muss mindestens drei Linien geben
            else if (iRow == pTableCellArrArr.length - 2 && pTableCellArrArr.length > 2)
            {
                appendLine(strBuff, pFussTrennMode, leerTrennLinienStr, einfachTrennLinienStr,
                        doppelTrennlinienStr);

                if (pFussTrennMode > NO_LINE)
                {
                    strBuff.append('\n');
                }
            }
            else if (iRow < pTableCellArrArr.length - 1)
            {
                // nicht letzter Schleifendurchlauf
                // Trenn-Linie zwischen den Zeilen
                appendLine(strBuff, pTrennMode, leerTrennLinienStr, einfachTrennLinienStr,
                        doppelTrennlinienStr);

                if (pTrennMode > NO_LINE)
                {
                    strBuff.append('\n');
                }
            }
        }

        // Fuss-Linie
        appendLine(strBuff, pFussMode, leerTrennLinienStr, einfachTrennLinienStr,
                doppelTrennlinienStr);

        if (pFussMode > NO_LINE)
        {
            strBuff.append('\n');
        }
        return strBuff.toString();
    }

    /**
     * Auslayouten mit colspan. <br>
     * maximale H�he der Zellen einer Zeile bestimmen<br>
     * maximale Breite der Zellen einer Spalte bestimmen<br>
     * <br>
     * Algorithmus:<br>
     * Es werden alle Zellen beginnend mit dem kleinsten colspan (1)
     * durchlaufen. <br>
     * <br>
     * Bei colspan == 1 wird die maximale Breite in das Merk-Array pMaxWidthArr
     * eingetragen.<br>
     * <br>
     * Bei h�herem colspan wird die vorhandene Breite durch andere Zellen
     * ausgerechnet. Wenn genug Platz f�r die colspan-Spalte vorhanden ist, wird
     * nichts getan.<br>
     * Wenn aber nicht genug Platz vorhanden ist, wird der notwendige Platz auf
     * die anderen Spalten verteilt.<br>
     * Dann wird der zu bearbeitende colspan erh�ht und solannge wiederholt, bis
     * alle Zellen verarbeitet wurden.<br>
     * 
     * @param pTableCellArrArr
     * @param pMaxHeightArr
     * @param pMaxWidthArr
     */
    private static void computeMaxHeightWidthColspan(
            Cell[][] pTableCellArrArr,
            int[] pMaxHeightArr,
            int[] pMaxWidthArr)
    {
        // Mindesth�he 1 einstellen
        for (int i = 0; i < pMaxHeightArr.length; i++)
        {
            pMaxHeightArr[i] = 1;
        }

        // Durch das colspan-Feature ist es zur Laufzeit-Optimierung notwendig,
        // erst mal alle H�hen zu bestimmen
        for (int iRow = 0; iRow < pTableCellArrArr.length; iRow++)
        {
            for (int iCol = 0; iCol < pTableCellArrArr[iRow].length; iCol++)
            {
                if (pTableCellArrArr[iRow][iCol] != null)
                {
                    // Vermerken maximale H�he
                    pMaxHeightArr[iRow] = Math.max(pMaxHeightArr[iRow],
                            pTableCellArrArr[iRow][iCol].getHeight());
                }
            }
        }

        // Algorithmus mit Beachtung colspan
        int colspanLevel = 0;
        boolean allCellsComputed = false;

        // ein colspan-Level nach dem anderen aufw�rts abarbeiten
        while (!allCellsComputed)
        {
            colspanLevel++;
            allCellsComputed = true;
            for (int iRow = 0; iRow < pTableCellArrArr.length; iRow++)
            {
                int iColspanOffset = 0;

                for (int iCol = 0; iColspanOffset < pMaxWidthArr.length
                        && iCol < pTableCellArrArr[iRow].length; iCol++)
                {
                    if (pTableCellArrArr[iRow][iCol] != null)
                    {
                        if (colspanLevel == pTableCellArrArr[iRow][iCol].getColspan())
                        {
                            if (colspanLevel == 1)
                            {
                                // Vermerken maximale Breite
                                pMaxWidthArr[iCol] = Math.max(pMaxWidthArr[iCol],
                                        pTableCellArrArr[iRow][iCol].getWidth());
                            }
                            else
                            {
                                // Sammeln der Breiten der �berspannten Spalten
                                int istBreite = 0;

                                for (int iColspan = 0; iColspan < colspanLevel
                                        && iCol + iColspan < pMaxWidthArr.length; iColspan++)
                                {
                                    if (iColspan > 0)
                                    {
                                        // L�cke mitz�hlen
                                        istBreite++;
                                    }
                                    final int colspanWidthOffset = iColspan + iColspanOffset;
                                    final int colspaOffsWidth = pMaxWidthArr[colspanWidthOffset];

                                    istBreite += colspaOffsWidth;
                                }
                                int colWidth = pTableCellArrArr[iRow][iCol].getWidth();

                                if (istBreite < colWidth)
                                {
                                    // der Platz reicht nicht, es muss gespreizt
                                    // werden
                                    // gleichm�ssig verteilen
                                    int zuschlag = (int) Math.round(Math.ceil((colWidth - istBreite)
                                            / colspanLevel));

                                    istBreite = 0;

                                    for (int iColspan = 0; istBreite < colWidth
                                            && iColspan < colspanLevel
                                            && iCol + iColspan < pMaxWidthArr.length; iColspan++)
                                    {
                                        if (iColspan > 0)
                                        {
                                            // L�cke mitz�hlen
                                            istBreite++;
                                        }
                                        final int colspanWidthOffset = iColspan + iColspanOffset;

                                        pMaxWidthArr[colspanWidthOffset] += zuschlag; // hier
                                                                                      // was
                                                                                      // dazu
                                        final int colspaOffsWidth = pMaxWidthArr[colspanWidthOffset];

                                        istBreite += colspaOffsWidth; // das
                                                                      // erh�hte
                                                                      // aufaddieren
                                    }
                                }

                            }
                        }

                        else if (colspanLevel < pTableCellArrArr[iRow][iCol].getWidth())
                        {
                            // es gibt noch Zellen mit h�herem colspan
                            allCellsComputed = false;
                        }
                    }

                    // um den colspan weiterschalten
                    iColspanOffset += pTableCellArrArr[iRow][iCol].getColspan();
                }
            }
        }

    }

    // -----------------------------------------------------------------------------------------
    //
    // Util - Methoden f�r interne Verwendung
    //
    // -----------------------------------------------------------------------------------------

    /**
     * Trennlinie entsprechend der gew�hlten Linien-Betriebsart an den per
     * Parameter �bergebenen Stringbuffer anh�ngen
     * 
     * @param strBuff
     * @param pKopfMode
     * @param pLeerTrennLinienStr
     * @param pEinfachTrennLinienStr
     * @param pDoppelTrennlinienStr
     */
    private static void appendLine(
            StringBuffer strBuff,
            int pKopfMode,
            final String pLeerTrennLinienStr,
            final String pEinfachTrennLinienStr,
            final String pDoppelTrennlinienStr)
    {
        switch (pKopfMode)
        {
        case EMPTY_LINE:
            strBuff.append(pLeerTrennLinienStr);

            break;

        case SINGLE_LINE:
            strBuff.append(pEinfachTrennLinienStr);

            break;

        case DOUBLE_LINE:
            strBuff.append(pDoppelTrennlinienStr);

            break;

        default: // NO_LINE
            break;
        }
    }

    /**
     * Trennlinie
     * 
     * <pre>
     * |   |   |   |
     * </pre>
     * 
     * erzeugen
     * 
     * @param maxWidthArr
     *            Spalten-Breiten
     * @return Linie als String
     */
    private static String createLeerTrennLinie(
            int[] maxWidthArr)
    {
        StringBuffer strBuff = new StringBuffer();

        strBuff.append('|');

        for (int i = 0; i < maxWidthArr.length; i++)
        {
            strBuff.append(strReplicate(" ", maxWidthArr[i]));
            strBuff.append('|');
        }

        return strBuff.toString();
    }

    /**
     * Trennlinie
     * 
     * <pre>
     * +---+---+---+
     * </pre>
     * 
     * erzeugen
     * 
     * @param maxWidthArr
     *            Spalten-Breiten
     * @return Linie als String
     */
    private static String createEinfachTrennLinie(
            int[] maxWidthArr)
    {
        StringBuffer strBuff = new StringBuffer();

        strBuff.append('+');

        for (int i = 0; i < maxWidthArr.length; i++)
        {
            strBuff.append(strReplicate("-", maxWidthArr[i]));
            strBuff.append('+');
        }

        return strBuff.toString();
    }

    /**
     * Trennlinie
     * 
     * <pre>
     * +===+===+===+
     * </pre>
     * 
     * erzeugen
     * 
     * @param maxWidthArr
     *            Spalten-Breiten
     * @return Linie als String
     */
    private static String createDoppelTrennLinie(
            int[] maxWidthArr)
    {
        StringBuffer strBuff = new StringBuffer();

        strBuff.append('+');

        for (int i = 0; i < maxWidthArr.length; i++)
        {
            strBuff.append(strReplicate("=", maxWidthArr[i]));
            strBuff.append('+');
        }

        return strBuff.toString();
    }

    // ------------------------ Cell - Klasse ---------------------------------

    /**
     * Diese Klasse repr�sentiert eine Tabellenzelle . <br>
     * 
     * <br/>
     * <b>Author:</b> <a href="http://www.heinerkuecker.de"
     * target="_blank">Heiner K�cker</a><br/>
     * <br/>
     */
    public static class Cell
    {

        /**
         * Ausrichtung der Zelle . <br>
         * 
         * @see AsciTable#NO_ALIGNMENT
         * @see AsciTable#LEFT_ALIGNMENT
         * @see AsciTable#RIGHT_ALIGNMENT
         */
        int alignment;

        /**
         * wie viele Tabellen-Spalten die Zelle �berspannt . <br>
         * Minimum ist 1.
         */
        private int colspan = 1;

        /**
         * die Text-Zeilen dieser Zelle
         */
        String[] contentLineArr;

        /**
         * Konstruktor
         * 
         * @param pContent
         */
        public Cell(String pContent)
        {
            this.contentLineArr = splitForNewline(pContent);
        }

        /**
         * Setzen Zellen Text
         * 
         * @param pContent
         *            Text
         */
        public void setContent(String pContent)
        {
            this.contentLineArr = splitForNewline(pContent);
        }

        /**
         * Ausrichtung der Zelle setzen
         * 
         * @param pAlignment
         */
        public void setAlignment(int pAlignment)
        {
            this.alignment = pAlignment;
        }

        /**
         * Abfrage einer bestimmten Zeile dieser Zelle. Ist die Zeile nicht
         * vorhanden, wird ein Leerstring zur�ckgegeben.
         * 
         * @param pLineNr
         *            Zeilen-Nummer Basis Null
         * @return Zeilen-String
         */
        public String getLine(int pLineNr, int pWidth)
        {
            String retStr;

            if (pLineNr < this.contentLineArr.length)
            {
                retStr = this.contentLineArr[pLineNr];
            }
            else
            {
                // Zeilen-Nummer gr�sser als Zeilen-Maximum
                retStr = "";
            }
            switch (this.alignment)
            {
            case LEFT_ALIGNMENT:
                retStr = strAlign(retStr.trim(), pWidth);
                break;

            case RIGHT_ALIGNMENT:
                retStr = strLeftAlign(retStr.trim(), pWidth);
                break;

            default:
                retStr = strAlign(retStr, pWidth);
                break;
            }

            return retStr;
        }

        /**
         * H�he der Zelle in Zeilen
         * 
         * @return
         */
        public int getHeight()
        {
            return this.contentLineArr.length;
        }

        /**
         * Breite der Zelle in Zeichen
         * 
         * @return
         */
        public int getWidth()
        {
            return getMaxLength(this.contentLineArr);
        }

        /**
         * Abfragen wie viele Tabellen-Spalten die Zelle �berspannt
         * 
         * @return wie viele Spalten die Zelle �berspannt
         */
        public int getColspan()
        {
            return this.colspan;
        }

        /**
         * Setzen wie viele Tabellen-Spalten die Zelle �berspannt
         * 
         * @param pColspan
         *            zu setzender Wert &gt;= 1
         */
        public void setColspan(int pColspan)
        {
            if (pColspan < 1)
            {
                throw new IllegalArgumentException("colspan must greater than zero: " + pColspan);
            }
            this.colspan = pColspan;
        }

    }

    // --------------------- Util-Methoden �ffentlich
    // ---------------------------------

    /**
     * Auff�llen mit Spaces oder Abschneiden eines String auf die verlangte
     * L�nge
     * 
     * @param strPa
     *            auszurichtender String
     * @param intPaLength
     *            gew�nschte L�nge
     * @return ausgerichteter String
     * @author Heiner K�cker
     */
    public static String strAlign(String strPa, int intPaLength)
    {
        if (strPa == null)
        {
            // garbage in, garbage out
            return null;
        }

        if (strPa.length() > intPaLength)
        {
            return strPa.substring(0, intPaLength);
        }
        if (strPa.length() == intPaLength)
        {
            return strPa;
        }
        StringBuffer strBuff = new StringBuffer(strPa);

        while (strBuff.length() < intPaLength)
        {
            strBuff.append(" ");
        }
        return strBuff.toString();
    }// end method strAlign

    /**
     * Auff�llen mit Spaces am linken Rand oder Abschneiden eines String auf die
     * verlangte L�nge.
     * 
     * @param strPa
     *            auszurichtender String
     * @param intPaLength
     *            gew�nschte L�nge
     * @return ausgerichteter String
     * @author Heiner K�cker
     */
    public static String strLeftAlign(String strPa, int intPaLength)
    {
        if (strPa == null)
        {
            // garbage in, garbage out
            return null;
        }

        if (strPa.length() > intPaLength)
        {
            return strPa.substring(intPaLength);
        }
        if (strPa.length() == intPaLength)
        {
            return strPa;
        }
        StringBuffer strBuff = new StringBuffer(strPa);

        while (strBuff.length() < intPaLength)
        {
            strBuff.insert(0, " ");
        }
        return strBuff.toString();
    }// end method strLeftAlign

    /**
     * Wiederholen eines String entsprechend verlangter Anzahl
     * 
     * @param strPa
     *            zu wiederholender String
     * @param intPaCount
     *            Anzahl der Wiederholungen
     * @return wiederholter String
     * @author Heiner K�cker
     */
    public static String strReplicate(String strPa, int intPaCount)
    {
        if (strPa == null)
        {
            // garbage in, garbage out
            return null;
        }

        StringBuffer strBuff = new StringBuffer();

        for (int i = 0; i < intPaCount; i++)
        {
            strBuff.append(strPa);
        }
        return strBuff.toString();
    }// end method strReplicate

    /**
     * Den �bergebenen String in einzelne Zeilen unterteilen
     * 
     * @param pStr
     *            zu unterteilender String
     * @return String-Array mit den einzelnen Zeilen
     * @author Heiner K�cker
     */
    public static String[] splitForNewline(String pStr)
    {
        if (pStr == null)
        {
            return null; // garbage in, garbage out
        }
        if (pStr.length() < 1)
        {
            return new String[0];
        }

        ArrayList<String> arrList = new ArrayList<String>();

        StringBuffer strBuff = new StringBuffer();

        for (int i = 0; i < pStr.length(); i++)
        {
            char charTmp = pStr.charAt(i);

            switch (charTmp)
            {
            case (char) 13:
                // Carriage Return ignorieren
                break;

            case '\n':
                // Zeilenumbruch Newline
                // Zeile vermerken
                arrList.add(strBuff.toString());
                // neue Zeile
                strBuff = new StringBuffer();
                break;

            default:
                // Zeichen �bernehmen
                strBuff.append(charTmp);
                break;
            }
        }
        // letzte Zeile vermerken
        arrList.add(strBuff.toString());

        String[] retStrArr = arrList.toArray(new String[arrList.size()]);

        return retStrArr;
    }

    /**
     * Ermitteln des l�ngsten Strings in einem String-Array
     * 
     * @param pStrArr
     * @return
     */
    public static int getMaxLength(String[] pStrArr)
    {
        if (pStrArr == null)
        {
            return 0;
        }
        int retLength = 0;

        for (int i = 0; i < pStrArr.length; i++)
        {
            if (pStrArr[i] != null)
            {
                retLength = Math.max(retLength, pStrArr[i].length());
            }
        }
        return retLength;
    }

    // -----------------------------------------------------------------------------------------
    //
    // Komfort - Methoden f�r Kopf, Fuss und laufende Nummer
    //
    // -----------------------------------------------------------------------------------------

    /**
     * Ausstatten eines zweidimensionalen Tabellen-Zellen-Arrays mit einer
     * Kopf-Zeile
     * 
     * @param pTable
     *            mit Kopf auszustattende Tabelle
     * @param pHeaderStrArr
     *            String-Array mit den Kopf-Texten
     * @return ausgestattete Tabelle
     * 
     * 
     */
    public static Cell[][] addHeader(Cell[][] pTable, String[] pHeaderStrArr)
    {
        Cell[][] retCellArrArr = new Cell[pTable.length + 1][pTable[0].length];

        // Umkopieren Tabelle
        for (int i1 = 0; i1 < pTable.length; i1++)
        {
            for (int i2 = 0; i2 < retCellArrArr[i1].length; i2++)
            {
                retCellArrArr[i1 + 1][i2] = pTable[i1][i2];
            }
        }
        // Header aufsetzen
        for (int i = 0; i < retCellArrArr[0].length; i++)
        {
            retCellArrArr[0][i] = new Cell(pHeaderStrArr[i]);
        }
        return retCellArrArr;
    }

    /**
     * Ausstatten eines zweidimensionalen Tabellen-Zellen-Array�s mit einer
     * Fuss-Zeile
     * 
     * @param pTable
     *            mit Fuss auszustattende Tabelle
     * @param pFooterStrArr
     *            String-Array mit den Fuss-Texten
     * @return ausgestattete Tabelle
     * 
     */
    public static Cell[][] addFooter(Cell[][] pTable, String[] pFooterStrArr)
    {
        Cell[][] retCellArrArr = new Cell[pTable.length + 1][pTable[0].length];

        // Umkopieren Tabelle
        for (int i1 = 0; i1 < pTable.length; i1++)
        {
            for (int i2 = 0; i2 < retCellArrArr[i1].length; i2++)
            {
                retCellArrArr[i1][i2] = pTable[i1][i2];
            }
        }
        // Footer druntersetzen
        for (int i = 0; i < retCellArrArr[0].length; i++)
        {
            retCellArrArr[retCellArrArr.length - 1][i] = new Cell(pFooterStrArr[i]);
        }
        return retCellArrArr;
    }

    /**
     * Ausstatten einer Tabelle mit einer Spalte laufende Nummer
     * 
     * @param pTable
     *            mit laufender Nummer auszustattende Tabelle
     * @param pStartNr
     *            Start-Nummer (0 oder 1)
     * @param pTopGap
     *            oben freizuhaltende Zeilen (1 oder 0)
     * @param pHeaderStr
     *            Text f�r Kopf (optional)
     * @param pBottGap
     *            unten freizuhaltende Zeilen (1 oder 0)
     * @param pFooterStr
     *            Text f�r Fuss (optional)
     * @return ausgestattete Tabelle
     * 
     * 
     */
    public static Cell[][] addCountCol(// -
            Cell[][] pTable, // mit laufender Nummer auszustattende Tabelle
            int pStartNr, // Start-Nummer (0 oder 1)
            int pTopGap, // oben freizuhaltende Zeilen (1 oder 0)
            String pHeaderStr, // Text f�r Kopf (optional)
            int pBottGap, // unten freizuhaltende Zeilen (1 oder 0)
            String pFooterStr) // Text f�r Fuss (optional)
    {
        Cell[][] retCellArrArr = new Cell[pTable.length][pTable[0].length + 1];

        // Umkopieren Tabelle
        for (int i1 = 0; i1 < pTable.length; i1++)
        {
            for (int i2 = 0; i2 < pTable[i1].length; i2++)
            {
                retCellArrArr[i1][i2 + 1] = pTable[i1][i2];
            }
        }
        // z�hlen ab Start-Nummer
        int nrCnt = pStartNr;

        // L�cken oben und unten frei lassen
        for (int i = pTopGap; i < retCellArrArr.length - pBottGap; i++)
        {
            retCellArrArr[i][0] = new Cell("" + nrCnt);
            // rechtsb�ndig
            retCellArrArr[i][0].setAlignment(RIGHT_ALIGNMENT);
            nrCnt++;
        }
        // Text f�r Kopf
        if (pTopGap > 0 && pHeaderStr != null)
        {
            retCellArrArr[0][0] = new Cell(pHeaderStr);
        }
        // Text f�r Fuss
        if (pBottGap > 0 && pFooterStr != null)
        {
            retCellArrArr[retCellArrArr.length - 1][0] = new Cell(pFooterStr);
        }
        return retCellArrArr;
    }

}
