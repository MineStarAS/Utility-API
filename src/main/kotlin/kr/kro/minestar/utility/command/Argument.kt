package kr.kro.minestar.utility.command

import kr.kro.minestar.utility.string.remove

/**
 * CommandExecutor 를 상속받은 클래스에서 사용되는 enum class 에 상속되어 사용됩니다.
 */
interface Argument {

    /**
     * enum class 에 상속 되기에 필요한 변수
     */
    val name: String

    /**
     * 입력 값이 어떤 방식으로 들어가야 하는지 알려주는 변수
     *
     * 입력 값 표시는 '카멜 표기법'으로만 작성해야 합니다.
     *
     * 괄호 사용법 :
     *
     *      대괄호 [Value1/Value2/...] : '지정된 입력 값', 괄호 안에 있는 값만 입력 가능
     *
     *      홑화살괄호 <Value> : '임의의 입력 값' 또는 '목록이 있는 입력값'
     *
     *      중괄호 {Value} : '생략 가능한 입력 값', 마지막에 들어가는 게 적합하다
     *
     *      중괄호 {Value1/Value2/...} : '생략 가능한 지정된 입력 값'
     */
    val howToUse: String

    /**
     * 해당 펄미션을 가지고 있는 유저만 사용할 수 있게 합니다.
     */
    val permission: ArgumentPermission

    /**
     * [name] 외 [aliases]에 있는 값으로도 커맨드를 실행 할 수 있습니다.
     */
    val aliases : List<String>?

    /**
     * Argument 유효한 lastIndex 값 Set< Int > 를 구합니다.
     */
    fun validLastIndex(): Set<Int> {
        val hashSet = hashSetOf<Int>()
        if (howToUse == "") {
            hashSet.add(0)
            return hashSet.toSet()
        }
        val split = howToUse.split(' ')
        if (split.isEmpty()) hashSet.toSet()

        var index = split.size
        hashSet.add(index)
        for (string in split.reversed()) {
            if (string.contains('{') && string.contains('}')) {
                index--
                hashSet.add(index)
            } else break
        }
        return hashSet.toSet()
    }

    /**
     * 커맨드 사용법을 출력합니다.
     */
    fun howToUse(label: String) = "/$label $name $howToUse"

    /**
     * onCommand 메소드에서 when 문법에 진입하기 전에 '예시 코드' 와 같은 코드를 넣어
     * 에러를 방지 합니다.
     */
    fun isValid(args: Array<out String>) = validLastIndex().contains(args.lastIndex)

    /**
     * howToUse 값에서 '대괄호' 또는 '중괄호'로 이루어진 입력 값 타입에 '/' 가 있으면 List< String > 형태로 변환해 줍니다.
     */
    fun argList(index: Int): List<String> {
        val list = mutableListOf<String>()
        if (index < 1) return list.toList()
        val newIndex = index - 1
        val split = howToUse.split(' ')
        if (split.lastIndex < newIndex) return list.toList()
        val value = split[newIndex]
        val charList = value.toList()
        if (charList.first() != '[' || charList.last() != ']')
            if (charList.first() != '{' || charList.last() != '}') return list.toList()
        return value.remove('[').remove(']').remove('{').remove('}').split('/')
    }
}