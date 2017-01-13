package pl.generator.random;

public enum Host {

    GOOGLE("google.com"), STACK_OVERFLOW("stackoverflow.com"), FACEBOOK("facebook.com"), STACK_EXCHANGE("stackexchange.com"), MICROSOFT("microsoft.com"),
    SPOTIFY("spotify.com"), YOUTUBE("youtube.com"), WIKIPEDIA("wikipedia.org"), REDDIT("reddit.com"), ESET("eset.pl");

    private String name;

    Host(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
