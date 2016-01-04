package nl.capaxit.volleytest;


import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;


/**
 * Representatie van een station.
 *
 * @author Rob Vermeulen - AmbiSoft
 */
public class Station implements Comparator<Station> {
    public static final double DEFAULT_RADIUS = 1000;
    private static final long serialVersionUID = 8587968526616293591L;
    private final Set<String> synoniemen = new HashSet<String>();
    @Deprecated // Use synoniemen instead
    public boolean alias;
    @Deprecated // Use one of the names in the names field instead.
    private String naam;
    private String abbreviation;
    private Namen namen;
    private StationType stationType;
    private String code;
    private int UICCode;
    private double radius;
    private double naderenRadius;
    private String land;
    private boolean heeftFaciliteiten;
    private boolean heeftVertrektijden;

    public Station() {
    }

    public Namen getNamen() {
        if (namen != null) {
            return namen;
        }

        this.namen = new Namen(naam, naam, naam);
        return this.namen;
    }

    public String getLand() {
        return land;
    }

    public void setLand(final String land) {
        this.land = land;
    }

    public void setNamen(final Namen namen) {
        this.namen = namen;
    }

    public boolean isHeeftFaciliteiten() {
        return heeftFaciliteiten;
    }

    public void setHeeftFaciliteiten(final boolean heeftFaciliteiten) {
        this.heeftFaciliteiten = heeftFaciliteiten;
    }

    public boolean isHeeftVertrektijden() {
        return heeftVertrektijden;
    }

    public void setHeeftVertrektijden(final boolean heeftVertrektijden) {
        this.heeftVertrektijden = heeftVertrektijden;
    }

    public boolean matchSynonyms(final String prefix) {
        if (synoniemen.isEmpty()) {
            return false;
        }

        for (final String synonym : synoniemen) {
            if (synonym.toLowerCase().startsWith(prefix.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    public StationType getStationType() {
        return stationType;
    }

    public void setStationType(final StationType stationType) {
        this.stationType = stationType;
    }

    public String getCode() {
        return code != null ? code : abbreviation;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public int getUICCode() {
        return UICCode;
    }

    public void setUICCode(final int UICCode) {
        this.UICCode = UICCode;
    }

    public Set<String> getSynoniemen() {
        return synoniemen;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(final double radius) {
        this.radius = radius;
    }

    public double getNaderenRadius() {
        return naderenRadius;
    }

    public void setNaderenRadius(final double naderenRadius) {
        this.naderenRadius = naderenRadius;
    }

    public String getNaam() {
        return getNamen().getLang();
    }

    public String getAbbreviationOrNaamIfAbbrIsEmpty() {
        if (getCode() != null) {
            return getNaam();
        }

        return getCode();
    }

    @Deprecated // Remove if stationslijst comes from JSON service.
    public void setNaam(final String naam) {
        this.naam = naam;
    }

    /*public String getCode() {
        return getCode();
    }*/

    public void setAbbreviation(final String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public boolean isAlias() {
        return alias;
    }

    public boolean isAvt() {
        return heeftVertrektijden;
    }

    @Deprecated // Use JSON response.
    public void setAvt(boolean avt) {
        this.heeftVertrektijden = avt;
    }

    @Override
    public int compare(Station lhs, Station rhs) {
        return lhs.getNaam().compareTo(rhs.getNaam());
    }

    /**
     * Create and return a geofence id for this station made unique with the supplied mijn reis id.
     *
     * @param mijnReisId
     * @return
     */
    public String createGeofenceId(final long mijnReisId) {
        return mijnReisId + "-" + getNaam() + "-" + getCode();
    }

    @Override
    public String toString() {
        return getNaam();
    }

    public boolean matchesNameOrSynonym(final String naam) {
        if (naam == null) {
            return false;
        }

        if (naam.equalsIgnoreCase(getNaam())) {
            return true;
        }

        for (final String synonym : synoniemen) {
            if (naam.equalsIgnoreCase(synonym)) {
                return true;
            }
        }

        return false;
    }

    public static enum StationType {
        STOPTREIN_STATION("stoptreinstation"),
        KNOOPPUNT_STOPTREIN_STATION("knooppuntStoptreinstation"),
        SNELTREIN_STATION("sneltreinstation"),
        KNOOPPUNT_SNELTREIN_STATION("knooppuntSneltreinstation"),
        INTERCITY_STATION("intercitystation"),
        KNOOPPUNT_INTERCITY_STATION("knooppuntIntercitystation"),
        MEGA_STATION("megastation"),
        FACULTATIEF_STATION("facultatiefStation");

        private final String code;

        StationType(final String code) {
            this.code = code;
        }

        public static StationType fromCode(final String code) {
            for (final StationType stationType : values()) {
                if (stationType.code.equalsIgnoreCase(code)) {
                    return stationType;
                }
            }

            throw new IllegalArgumentException("No type station for [" + code + "]");
        }

        public String getCode() {
            return code;
        }
    }

    public static final class Namen implements Serializable {
        private final String lang;
        private final String kort;
        private final String middel;

        public Namen(final String lang, final String kort, final String middel) {
            this.lang = lang;
            this.kort = kort;
            this.middel = middel;
        }

        public static Namen allSame(final String naam) {
            return new Namen(naam, naam, naam);
        }

        public String getLang() {
            return lang;
        }

        public String getKort() {
            return kort;
        }

        public String getMiddel() {
            return middel;
        }
    }


}
