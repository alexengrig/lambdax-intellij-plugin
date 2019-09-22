public class X {
    public void x() {
        java.lang.Object value = java.util.Optional.of(new java.util.HashMap<java.lang.Object, java.lang.Object>())
                .map(<caret>map -> map.get(1))
                .orElse(0);
    }
}