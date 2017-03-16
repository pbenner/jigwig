/* Copyright (C) 2017 Philipp Benner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

class BbiHeader {

    long Magic;
    int  Version;
    int  ZoomLevels;
    long CtOffset;
    long DataOffset;
    long IndexOffset;
    int  FieldCould;
    int  DefinedFieldCount;
    long SqlOffset;
    long SummaryOffset;
    long UncompressBufSize;
    long ExtensionOffset;
    long NBasesCovered;
    long MinVal;
    long MaxVal;
    long SumData;
    long SumSquared;
    BbiHeaderZoom[] ZoomHeaders;
    long NBlocks;
  // offset positions
    long PtrCtOffset;
    long PtrDataOffset;
    long PtrIndexOffset;
    long PtrSqlOffset;
    long PtrSummaryOffset;
    long PtrUncompressBufSize;
    long PtrExtensionOffset;

}
