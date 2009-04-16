Public Class Dictionary

    '    <xs:element name="dictionary">
    '  <xs:complexType>
    '    <xs:sequence>
    '      <xs:element ref="entry" maxOccurs="unbounded"/>
    '    </xs:sequence>
    '    <xs:attribute name="language" type="xs:string"/>
    '    <xs:attribute name="version" type="xs:string"/>
    '  </xs:complexType>
    '</xs:element>

    Private _entries As Hashtable
    Private _language As String
    Private _version As String

    Public Property entries() As Hashtable
        Get
            Return _entries
        End Get
        Set(ByVal value As Hashtable)
            Me._entries = value
        End Set
    End Property

    Public Property language() As String
        Get
            Return _language
        End Get
        Set(ByVal value As String)
            Me._language = value
        End Set
    End Property

    Public Property version() As String
        Get
            Return _version
        End Get
        Set(ByVal value As String)
            Me._version = value
        End Set
    End Property


    Public Sub New(ByVal language As String, ByVal version As String)
        Me.language = language
        Me.version = version
        Me.entries = New Hashtable
    End Sub

    Public Sub addEntry(ByVal entry As Entry)
        entries.Add(entry.id, entry)
    End Sub

    Public Sub removeEntry(ByVal entry As Entry)
        entries.Remove(entry.id)
    End Sub

    Public Function getEntry(ByVal i As Integer) As Entry
        Return entries.Item(i)
    End Function

    Public Function getLessons() As List(Of String)
        Dim ret As List(Of String) = New List(Of String)
        ret.Add("All Vocabs")
        For Each ent As Entry In entries.Values
            If Not ret.Contains(ent.lesson) Then
                ret.Add(ent.lesson)
            End If
        Next
        ret.Sort()

        Return ret
    End Function

    Public Function getEntriesForLesson(ByVal les As String) As Hashtable
        Dim ret As Hashtable = New Hashtable

        For Each ent As Entry In entries.Values
            If ent.lesson.Equals(les) Then
                ret.Add(ent.id, ent)
            End If
        Next
        Return ret
    End Function

    Public Function toXML() As XElement
        Dim entry As String = ""
        
        Dim langAtt As XAttribute = New XAttribute("language", Me.language)
        Dim verAtt As XAttribute = New XAttribute("version", Me.version)
        Dim ret As XElement = <dictionary>
                              </dictionary>
        For Each elem As Entry In entries.Values
            ret.Add(elem.toXML)
        Next

        ret.Add(langAtt)
        ret.Add(verAtt)

        Return ret
    End Function
End Class
