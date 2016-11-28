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
 * </pre>
 */
package gq.baijie.simpleim.prototype.server.impl.vertx.codec;
