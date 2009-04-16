Imports System.Xml

Public Class Entry

    '    <xs:element name="entry">
    '  <xs:complexType>
    '    <xs:sequence>
    '      <xs:element ref="orth"/>
    '      <xs:element ref="sense" maxOccurs="unbounded"/>
    '      <xs:element ref="lesson"/>
    '    </xs:sequence>
    '    <xs:attribute name="id" type="xs:string" use="required"/>
    '  </xs:complexType>
    '</xs:element>

    Private _id As String
    Private _orth As String
    Private _senses As List(Of Sense)
    Private _lesson As String

    Public Property id() As String
        Get
            Return _id
        End Get
        Set(ByVal value As String)
            Me._id = value
        End Set
    End Property

    Public Property orth() As String
        Get
            Return _orth
        End Get
        Set(ByVal value As String)
            Me._orth = value
        End Set
    End Property

    Public Property senses() As List(Of Sense)
        Get
            Return _senses
        End Get
        Set(ByVal value As List(Of Sense))
            Me._senses = value
        End Set
    End Property

    Public Property lesson() As String
        Get
            Return _lesson
        End Get
        Set(ByVal value As String)
            Me._lesson = value
        End Set
    End Property

    Public Sub New(ByVal id As String, ByVal orth As String, ByVal lesson As String)
        Me.id = id
        Me.orth = orth
        Me.lesson = lesson
        Me.senses = New List(Of Sense)
    End Sub

    Public Sub addSense(ByVal sense As Sense)
        Me.senses.Add(sense)
    End Sub

    Public Function getSense(ByVal i As Integer) As Sense
        Return senses.Item(i)
    End Function

    Public Function toXML() As XElement

        Dim idAtt As XAttribute = New XAttribute("id", Me.id)
        Dim ret As XElement = <entry>
                              </entry>
        ret.Add(<orth><%= Me.orth %></orth>)

        For Each elem In senses
            ret.Add(elem.toXML)
        Next

        ret.Add(<lesson><%= Me.lesson %></lesson>)

        ret.Add(idAtt)
        Return ret
    End Function


End Class
