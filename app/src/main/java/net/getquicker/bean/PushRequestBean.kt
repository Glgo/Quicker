package net.getquicker.bean

import net.getquicker.utils.SpUtil

/**
 *  author : Clay
 *  date : 2022/05/06 16:37:23
 *  @link https://getquicker.net/kc/manual/doc/connection
 *  description :
 *  参数说明：
    toUser：自己的账号Email地址。
    code：前面步骤中设置的推送验证码。
    toDevice（可选）：目标设备（电脑）名。
        留空或不提供此参数：表示发送给当前活动主机（显示绿点）；
         * ：表示发送给当前账号所有主机（此处与V1版本中不同）；
        某个特定的电脑主机名称：发送给指定的电脑（必须已建立好长连接）。
    operation（可选）：操作类型，默认为 copy 。可选值：
        copy  将内容复制到剪贴板
        paste  将内容粘贴到当前窗口
        action 运行动作。此时通过“action”参数传入动作名称或ID，通过“data”参数传入动作参数（可选）。
        open 打开网址。此时通过data参数传入要打开的网址。
        input sendkeys 模拟输入内容。此时通过data传入“模拟按键B”语法格式的内容。(1.27.3+版本请使用sendkeys)
        inputtext 模拟输入文本（原样输入）。
        inputscript 多步骤输入。组合多个键盘和鼠标输入步骤，参考文档。 （1.28.16+） @link https://getquicker.net/kc/help/doc/inputscript
        downloadfile 下载文件。下载data参数中给定的文件网址（单个）。 （1.28.16+）
    wait（可选）：是否等待（动作）返回结果，可选值为 true 或 false ，默认为 false 。如果等待，则等待Quicker运行动作并取回动作的返回结果。
    maxWaitMs（可选）：最多等待毫秒数，默认为3000（最大值为30,000毫秒）。
    data：数据内容。根据操作类型（operation）：
        copy paste input 时，表示要复制、粘贴或键入的内容。
        action 时，表示动作的参数。此时data参数可选。
        open 时，可以为网址或文件路径。
    action：在操作类型（operation）为 action 时，提供动作的id或名称（使用动作名称时，不能有重复名称的动作）。
    txt：（可选）是否返回纯文本格式的结果。默认为否，返回json格式。
 */
data class PushRequestBean(
    val operation: String,
    val data: String? = null,
    val action: String? = null,
    val wait: Boolean = false,
    val toDevice: String = SpUtil.toDevices,
    val toUser: String = SpUtil.email,
    val code: String = SpUtil.pushCode,
    val maxWaitMs: Int = 3000,
    val txt: Boolean = false
)