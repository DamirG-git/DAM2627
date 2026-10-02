package dg2627activities.U1.Ej3;

import java.util.List;
import java.util.Optional;

public class U1Ex3 {
    enum SculptureMaterial {
        IRON, BRONZE, MARBLE
    }

    enum PaintingType {
        OIL, PASTEL, WATERCOLOUR
    }

    enum ArtworkStyle {
        Neoclassical, GrecoRoman, Cubist
    }


    class Author {
        public Author(String nationality, String name, List<Artwork> artworks) {
            this.nationality = nationality;
            this.name = name;
            this.artworks = artworks;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Artwork> getArtworks() {
            return artworks;
        }

        public void setArtworks(List<Artwork> artworks) {
            this.artworks = artworks;
        }

        public String getNationality() {
            return nationality;
        }

        public void setNationality(String nationality) {
            this.nationality = nationality;
        }

        String name;
        String nationality;
        List<Artwork> artworks;
    }

    class Artwork {
        private String title;

        public Artwork(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }


    }

    class Painting extends Artwork {
        public Painting(PaintingType type, String format, String title) {
            super(title);
            this.type = type;
            this.format = format;
        }

        private PaintingType type;
        private String format;

        public PaintingType getType() {
            return type;
        }

        public void setType(PaintingType type) {
            this.type = type;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }


    }

    class Sculpture extends Artwork {
        public Sculpture(SculptureMaterial sculptureMaterial, ArtworkStyle artworkStyle, String title) {
            super(title);
            this.sculptureMaterial = sculptureMaterial;
            this.artworkStyle = artworkStyle;
        }

        private SculptureMaterial sculptureMaterial;
        private ArtworkStyle artworkStyle;

        public ArtworkStyle getArtworkStyle() {
            return artworkStyle;
        }

        public void setArtworkStyle(ArtworkStyle artworkStyle) {
            this.artworkStyle = artworkStyle;
        }

        public SculptureMaterial getSculptureMaterial() {
            return sculptureMaterial;
        }

        public void setSculptureMaterial(SculptureMaterial sculptureMaterial) {
            this.sculptureMaterial = sculptureMaterial;
        }


    }

    class Gallery {
        public Gallery(String name) {
            this.name = name;
        }

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


        private List<Artwork> artworks;

        public List<Artwork> getArtworks() {
            return artworks;
        }

        public void setArtworks(List<Artwork> artworks) {
            this.artworks = artworks;
        }

        public void addArtwork(Artwork artwork) {
            artworks.add(artwork);
        }

        public void removeArtwork(Artwork artwork) {
            this.artworks.remove(artwork);
        }

        public Optional<Artwork> findArtwork(String title) {
            return artworks.stream().filter(artwork -> artwork.getTitle().equals(title)).findFirst();
        }

    }

    class Museum {
        public Museum(String name, String city, String country, String address) {
            this.name = name;
            this.city = city;
            this.country = country;
            this.address = address;
        }

        private String name;
        private String address;
        private String city;
        private String country;

        public List<Artwork> getArtworks() {
            return artworks;
        }

        public void setArtworks(List<Artwork> artworks) {
            this.artworks = artworks;
        }

        public void addArtwork(Artwork artwork) {
            artworks.add(artwork);
        }

        public void removeArtwork(Artwork artwork) {
            this.artworks.remove(artwork);
        }

        public Optional<Artwork> findArtwork(String title) {
            return artworks.stream().filter(artwork -> artwork.getTitle().equals(title)).findFirst();
        }

        private List<Artwork> artworks;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

    }

/*
    public class DisplayTestData {
        DisplayTestData() {
            museums = new ArrayList<>();
            museums.add(new Museum("Museum1", "City1", "Country1", "Address1"));
            museums.add(new Museum("Museum2", "City2", "Country2", "Address2"));
            museums.add(new Museum("Museum3", "City3", "Country3", "Address3"));


        }

        List<Gallery>
        List<Museum> museums;


    }
*/
}
