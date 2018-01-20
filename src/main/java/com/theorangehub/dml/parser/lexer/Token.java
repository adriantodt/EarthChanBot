/*
 *  Copyright 2017 An Tran and Adrian Todt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.theorangehub.dml.parser.lexer;

public final class Token {
    private final Position position;
    private final String str;
    private final TokenType type;

    public Token(Position position, TokenType type) {
        this(position, type, null);
    }

    public Token(Position position, TokenType type, String str) {
        this.position = position;
        this.type = type;
        this.str = str;
    }

    @Override
    public String toString() {
        return str == null ? type.toString() : type.toString() + "(" + str + ")";
    }

    public Position getPosition() {
        return position;
    }

    public String getString() {
        return str;
    }

    public TokenType getType() {
        return type;
    }
}