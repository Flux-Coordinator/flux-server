package repositories.utils;

public class SqlNativeHelper {
    public static String getTruncateAllTables() {
        return "TRUNCATE TABLE public.anchor\n" +
                "    RESTART IDENTITY\n" +
                "    CASCADE;\n" +
                "TRUNCATE TABLE public.anchorposition\n" +
                "    RESTART IDENTITY\n" +
                "    CASCADE;\n" +
                "TRUNCATE TABLE public.measurement\n" +
                "    RESTART IDENTITY\n" +
                "    CASCADE;\n" +
                "TRUNCATE TABLE public.measurementstate\n" +
                "    RESTART IDENTITY\n" +
                "    CASCADE;\n" +
                "TRUNCATE TABLE public.position\n" +
                "    RESTART IDENTITY\n" +
                "    CASCADE;\n" +
                "TRUNCATE TABLE public.project\n" +
                "    RESTART IDENTITY\n" +
                "    CASCADE;\n" +
                "TRUNCATE TABLE public.reading\n" +
                "    RESTART IDENTITY\n" +
                "    CASCADE;\n" +
                "TRUNCATE TABLE public.room\n" +
                "    RESTART IDENTITY\n" +
                "    CASCADE;";
    }
}
