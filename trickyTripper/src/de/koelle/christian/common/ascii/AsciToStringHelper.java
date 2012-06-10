package de.koelle.christian.common.ascii;

import java.util.regex.Pattern;

/**
 * <p>
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++
 * </p>
 * The original version of this class has been provided by Heiner Kücker via his
 * homepage: <a href=" http://www.heinerkuecker.de/AsciTable.html">
 * http://www.heinerkuecker.de/AsciTable.html</a>
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
 * Helper class to layout a given object as ascii table.
 * <p/>
 * 
 * @author Heiner Kücker
 */
public class AsciToStringHelper {

    private static final Pattern numberPattern = Pattern.compile("^(-)?[0-9]+((,|\\.)([0-9]){2,})?$");

    public static String asciToString( // --
            final AsciTableLayoutableInterface[] pObjArr)
    {
        if (pObjArr != null)
        {
            // nur für performance test: return "";
        }
        if (pObjArr == null)
        {
            return " is null ";
        }
        if (pObjArr.length < 1)
        {
            return " is empty ";
        }
        AsciTableLayoutableInterface pObj = pObjArr[0];
        if (pObj == null)
        {
            return " first entry is null ";
        }
        AsciTable.Cell[][] table = new AsciTable.Cell[pObjArr.length + 1][pObj.asciTableColumnNames().length];

        for (int i = 0; i < pObj.asciTableColumnNames().length; i++)
        {
            table[0][i] = new AsciTable.Cell(pObj.asciTableColumnNames()[i]);
            table[0][i].setAlignment(AsciTable.LEFT_ALIGNMENT); /* added */
        }
        for (int i = 0; i < pObjArr.length; i++)
        {
            pObj = pObjArr[i];
            if (pObj != null)
            {
                final int j = i + 1;
                for (int k = 0; k < pObj.asciTableColumnNames().length; k++)
                {
                    final Object cellValue = pObj.asciTableColumnContent(k);
                    final String cellStr;
                    if (cellValue instanceof AsciTableLayoutableInterface[]) {
                        cellStr =
                                cutRight(
                                        asciToString((AsciTableLayoutableInterface[]) cellValue),
                                        "\n");
                    }
                    else {
                        cellStr = (cellValue != null) ? String.valueOf(cellValue).trim() : String.valueOf(cellValue);
                    }
                    table[j][k] = new AsciTable.Cell(cellStr);
                    if (isNumeric(cellStr)) {
                        table[j][k].setAlignment(AsciTable.RIGHT_ALIGNMENT);
                    }
                }
            }
        }
        return AsciTable.buildLayout(// --
                table, AsciTable.SINGLE_LINE, // --
                AsciTable.SINGLE_LINE, // --
                AsciTable.SINGLE_LINE, // --
                AsciTable.SINGLE_LINE, // --
                AsciTable.SINGLE_LINE);
    }

    private static boolean isNumeric(final Object value) {
        return value instanceof String && (isNumeric2((String) value));
    }

    private static boolean isNumeric2(String value) {
        return value != null && numberPattern.matcher(value).matches();
    }

    /**
     * Teilstring von String rechts abschneiden, wenn möglich
     * 
     * @param srcStr
     *            Original-String
     * @param cutStr
     *            abzuschneidender String
     * @return abgeschnittener String
     */
    public static String cutRight(
            final String srcStr,
            final String cutStr) {
        if (srcStr == null || cutStr == null)
        {
            return srcStr;
        }

        String retStr = srcStr;

        if (retStr.endsWith(cutStr))
        {
            retStr = retStr.substring(0, retStr.length() - cutStr.length()); // cutStr
                                                                             // abschneiden
        }

        return retStr;
    }// end method cutRight

}
