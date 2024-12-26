package store.aurora.book.dto;



public class AuthorRoleDTO {
    private Long id;
    private Role role;

    public enum Role {
        AUTHOR("지은이"),
        EDITOR("엮은이"),
        TRANSLATOR("옮긴이"),
        ADAPTER("편역"),
        ILLUSTRATOR("그림"),
        SUPERVISOR("감수"),
        PLANNER("기획"),
        CHARACTER("캐릭터"),
        COMPOSER("구성"),
        MAP("지도"),
        DRAWING("삽화"),
        ILLUSTRATION("일러스트"),
        PHOTOGRAPHER("사진"),
        ORIGINAL("원작"),
        WRITER("글"),
        COMMENTATOR("해설");

        private final String koreanName;

        Role(String koreanName) {
            this.koreanName = koreanName;
        }
        public String getKoreanName() {
            return koreanName;
        }
    }
}
