package com.sy.tool;

public class AllEnum {
    public enum SizeEnum {
        One(10485760L, 1), Twin(52428800L, 2),
        three(104857600L, 3), four(209715200L, 4),
        five(524288000L, 5), six(734003200L, 6),
        seven(1073741824L, 7), eight(2147483648L, 8),
        nine(10737418240L, 9), ten(21474836480L, 10);
        private Long size;
        private Integer index;


        SizeEnum(Long size, Integer index) {
            this.size = size;
            this.index = index;
        }

        static Long getSize(Integer index) {
            for (SizeEnum Type : SizeEnum.values()) {
                if (index.equals(Type.getIndex())) {
                    return Type.size;
                }
            }
            return null;
        }

        static Integer getIndex(Long size) {
            for (SizeEnum Type : SizeEnum.values()) {
                if (size.equals(Type.getsize())) {
                    return Type.index;
                }
            }
            return null;
        }
        public Integer getIndex() {
            return index;
        }

        public Long getsize() {
            return size;
        }

    }
    public static Long SizeEnum(Integer index) {
        return SizeEnum.getSize(index);
    }

    //    索引
    public static Integer SizeEnumNum(Long size) {
        return SizeEnum.getIndex(size);
    }

}
