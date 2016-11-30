/**
 * <pre>
 * frame: length | record
 * length: int
 *
 * record: record id | record type | record data
 * record id: short
 * record type: 1 byte enum {
 * 1: AccountServerRequest,
 * 2: AccountServerResponse
 * 3: Message
 * }
 *
 * AccountServerRequest: request type | request data
 * request type: 1 byte enum {
 * 1: RegisterRequest,
 * 2: LoginRequest,
 * 3: LogoutRequest,
 * 4: GetOnlineUsersRequest
 * }
 *
 * AccountServerResponse: request record id | response data
 *
 * Message: (ReceiverId | FIELD_DELIMITER) * n | SenderId | FIELD_DELIMITER | Message | FIELD_DELIMITER
 * // UTF-8 doesn't contain FIELD_DELIMITER
 * FIELD_DELIMITER: 1 byte value = 0b1100_0000
 * ReceiverId: UTF-8 encoded String
 * SenderId: UTF-8 encoded String
 * Message: UTF-8 encoded String
 *
 * </pre>
 */
package gq.baijie.simpleim.prototype.impl.vertx.codec;
